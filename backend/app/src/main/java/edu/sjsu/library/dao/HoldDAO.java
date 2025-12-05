package edu.sjsu.library.dao;

import edu.sjsu.library.models.Hold;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class HoldDAO {

    private final JdbcTemplate jdbc;

    public HoldDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS holds (
                id SERIAL PRIMARY KEY,
                userid INTEGER NOT NULL,
                titleid INTEGER NOT NULL,
                copyid INTEGER NULL,
                status VARCHAR(20) NOT NULL,
                placedat TIMESTAMP NOT NULL,
                readyat TIMESTAMP NULL,
                pickupexpire TIMESTAMP NULL,
                position INTEGER NOT NULL
            )
            """;
        jdbc.execute(sql);
    }

    private Hold mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        // Handle nullable copyid
        Integer copyId = rs.getObject("copyid") != null ? rs.getInt("copyid") : null;
        
        return new Hold(
            rs.getInt("id"),
            rs.getInt("userid"),
            rs.getInt("titleid"),
            copyId,
            Hold.HoldStatus.valueOf(rs.getString("status").trim().toUpperCase()),
            rs.getTimestamp("placedat").toLocalDateTime(),
            rs.getTimestamp("readyat") != null ? rs.getTimestamp("readyat").toLocalDateTime() : null,
            rs.getTimestamp("pickupexpire") != null ? rs.getTimestamp("pickupexpire").toLocalDateTime() : null,
            rs.getInt("position")
        );
    }

    public List<Hold> findAll() {
        return jdbc.query(
            "SELECT * FROM holds ORDER BY id",
            this::mapRow
        );
    }

    public Hold findById(int id) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM holds WHERE id = ?",
                this::mapRow,
                id
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Hold> findByUser(int userId) {
        return jdbc.query(
            "SELECT * FROM holds WHERE userid = ? ORDER BY position",
            this::mapRow,
            userId
        );
    }

    public int insert(Hold h) {
        Integer newId = jdbc.queryForObject(
            """
            INSERT INTO holds (userid, titleid, copyid, status, placedat, readyat, pickupexpire, position)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """,
            Integer.class,
            h.getUserID(),
            h.getTitleID(),
            h.getCopyID(),
            h.getStatus().name(),
            Timestamp.valueOf(h.getPlacedAt()),
            h.getReadyAt() != null ? Timestamp.valueOf(h.getReadyAt()) : null,
            h.getPickupExpire() != null ? Timestamp.valueOf(h.getPickupExpire()) : null,
            h.getPosition()
        );
        return newId;
    }

    public int update(Hold h) {
        return jdbc.update(
            """
            UPDATE holds
            SET userid = ?, titleid = ?, copyid = ?, status = ?,
                placedat = ?, readyat = ?, pickupexpire = ?, position = ?
            WHERE id = ?
            """,
            h.getUserID(),
            h.getTitleID(),
            h.getCopyID(),
            h.getStatus().name(),
            Timestamp.valueOf(h.getPlacedAt()),
            h.getReadyAt() != null ? Timestamp.valueOf(h.getReadyAt()) : null,
            h.getPickupExpire() != null ? Timestamp.valueOf(h.getPickupExpire()) : null,
            h.getPosition(),
            h.getHoldID()
        );
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM holds WHERE id = ?", id);
    }

    // Get the next position in queue for a title.
    public int getNextPosition(int titleID) {
        try {
            Integer maxPosition = jdbc.queryForObject(
                "SELECT MAX(position) FROM holds WHERE titleid = ? AND status IN ('QUEUED', 'READY')",
                Integer.class,
                titleID
            );
            return maxPosition != null ? maxPosition + 1 : 1;
        } catch (Exception e) {
            return 1; // First hold for this title
        }
    }

    // Find active hold by user and title.
    public Hold findActiveHoldByUserAndTitle(int userID, int titleID) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM holds WHERE userid = ? AND titleid = ? AND status IN ('QUEUED', 'READY')",
                this::mapRow,
                userID, titleID
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Find next hold in queue for a title.
    public Hold findNextHoldForTitle(int titleID) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM holds WHERE titleid = ? AND status = 'QUEUED' ORDER BY position LIMIT 1",
                this::mapRow,
                titleID
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Find holds by title.
    public List<Hold> findByTitle(int titleID) {
        return jdbc.query(
            "SELECT * FROM holds WHERE titleid = ? ORDER BY position",
            this::mapRow,
            titleID
        );
    }

    // Find holds by title ordered by position (only looks at active holds).
    public List<Hold> findByTitleOrderedByPosition(int titleID) {
        return jdbc.query(
            "SELECT * FROM holds WHERE titleid = ? AND status IN ('QUEUED', 'READY') ORDER BY position",
            this::mapRow,
            titleID
        );
    }

    // Find expired holds.
    public List<Hold> findExpiredHolds() {
        return jdbc.query(
            "SELECT * FROM holds WHERE status = 'READY' AND pickupexpire < NOW()",
            this::mapRow
        );
    }

    // Find holds by copy (to check if copy is referenced by any hold).
    public List<Hold> findByCopy(int copyID) {
        return jdbc.query(
            "SELECT * FROM holds WHERE copyid = ?",
            this::mapRow,
            copyID
        );
    }

    // Find active holds by copy (QUEUED or READY).
    public Hold findActiveHoldByCopy(int copyID) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM holds WHERE copyid = ? AND status IN ('QUEUED', 'READY') LIMIT 1",
                this::mapRow,
                copyID
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }
}
