package edu.sjsu.library.dao;

import edu.sjsu.library.models.Fine;
import edu.sjsu.library.models.Fine.FineStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class FineDAO {

    private final JdbcTemplate jdbc;

    public FineDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS fines (
                id SERIAL PRIMARY KEY,
                userid INTEGER NOT NULL,
                loanid INTEGER NOT NULL,
                amount NUMERIC(10,2) NOT NULL,
                finedate TIMESTAMP NOT NULL,
                reason VARCHAR(255) NOT NULL,
                status VARCHAR(20) NOT NULL
            )
            """;
        jdbc.execute(sql);
    }

    private Fine mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Fine(
            rs.getInt("id"),
            rs.getInt("userid"),
            rs.getInt("loanid"),
            rs.getBigDecimal("amount"),
            rs.getTimestamp("finedate").toLocalDateTime(),
            rs.getString("reason"),
            FineStatus.valueOf(rs.getString("status").trim().toUpperCase())
        );
    }

    public List<Fine> findAll() {
        return jdbc.query("SELECT * FROM fines ORDER BY id", this::mapRow);
    }

    public Fine findById(int id) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM fines WHERE id = ?",
                this::mapRow,
                id
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Fine> findByUser(int userId) {
        return jdbc.query(
            "SELECT * FROM fines WHERE userid = ? ORDER BY finedate DESC",
            this::mapRow,
            userId
        );
    }

    public List<Fine> findByUserAndStatus(int userId, FineStatus status) {
        return jdbc.query(
            "SELECT * FROM fines WHERE userid = ? AND status = ? ORDER BY finedate DESC",
            this::mapRow,
            userId,
            status.name()
        );
    }

    public int insert(Fine f) {
        Integer newId = jdbc.queryForObject(
            """
            INSERT INTO fines (userid, loanid, amount, finedate, reason, status)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """,
            Integer.class,
            f.getUserID(),
            f.getLoanID(),
            f.getAmount(),
            Timestamp.valueOf(f.getFineDate()),
            f.getReason(),
            f.getStatus().name()
        );
        return newId;
    }

    public int update(Fine f) {
        return jdbc.update(
            """
            UPDATE fines
            SET userid = ?, loanid = ?, amount = ?, finedate = ?, reason = ?, status = ?
            WHERE id = ?
            """,
            f.getUserID(),
            f.getLoanID(),
            f.getAmount(),
            Timestamp.valueOf(f.getFineDate()),
            f.getReason(),
            f.getStatus().name(),
            f.getFineID()
        );
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM fines WHERE id = ?", id);
    }
}
