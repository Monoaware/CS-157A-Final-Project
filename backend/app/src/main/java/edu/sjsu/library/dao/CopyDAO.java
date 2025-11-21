package edu.sjsu.library.dao;

import edu.sjsu.library.models.Copy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CopyDAO {

    private final JdbcTemplate jdbc;

    public CopyDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Create table
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS copies (
                copyid SERIAL PRIMARY KEY,
                titleid INTEGER NOT NULL,
                barcode VARCHAR(100) UNIQUE NOT NULL,
                status VARCHAR(20) NOT NULL,
                location VARCHAR(100),
                isvisible BOOLEAN NOT NULL
            )
            """;
        jdbc.execute(sql);
    }

    // Row mapper
    private Copy mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Copy(
            rs.getInt("copyid"),
            rs.getInt("titleid"),
            rs.getString("barcode"),
            Copy.CopyStatus.valueOf(rs.getString("status").trim().toUpperCase()),
            rs.getString("location"),
            rs.getBoolean("isvisible")
        );
    }

    // Get all copies
    public List<Copy> findAll() {
        return jdbc.query("SELECT * FROM copies ORDER BY copyid", this::mapRow);
    }

    // Find by ID
    public Copy findById(int id) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM copies WHERE copyid = ?",
                this::mapRow,
                id
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Find all copies for a title
    public List<Copy> findByTitle(int titleId) {
        return jdbc.query(
            "SELECT * FROM copies WHERE titleid = ? ORDER BY copyid",
            this::mapRow,
            titleId
        );
    }

    // Find by barcode
    public Copy findByBarcode(String barcode) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM copies WHERE barcode = ?",
                this::mapRow,
                barcode
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Insert a new copy
    public int insert(Copy c) {
        Integer newId = jdbc.queryForObject(
            """
            INSERT INTO copies (titleid, barcode, status, location, isvisible)
            VALUES (?, ?, ?, ?, ?)
            RETURNING copyid
            """,
            Integer.class,
            c.getTitleID(),
            c.getBarcode(),
            c.getStatus().name(),
            c.getLocation(),
            c.isVisible()
        );
        return newId;
    }

    // Update an existing copy
    public int update(Copy c) {
        return jdbc.update(
            """
            UPDATE copies
            SET titleid = ?, barcode = ?, status = ?, location = ?, isvisible = ?
            WHERE copyid = ?
            """,
            c.getTitleID(),
            c.getBarcode(),
            c.getStatus().name(),
            c.getLocation(),
            c.isVisible(),
            c.getCopyID()
        );
    }

    // Delete copy by ID
    public int delete(int id) {
        return jdbc.update("DELETE FROM copies WHERE copyid = ?", id);
    }
}
