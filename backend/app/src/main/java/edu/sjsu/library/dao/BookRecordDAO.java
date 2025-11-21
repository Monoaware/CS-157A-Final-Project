package edu.sjsu.library.dao;

import edu.sjsu.library.models.BookRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class BookRecordDAO {

    private final JdbcTemplate jdbc;

    public BookRecordDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Create table (PostgreSQL)
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS book_records (
                loanid SERIAL PRIMARY KEY,
                copyid INTEGER NOT NULL,
                userid INTEGER NOT NULL,
                checkoutdate TIMESTAMP NOT NULL,
                duedate TIMESTAMP NOT NULL,
                returndate TIMESTAMP,
                renewcount INTEGER NOT NULL
            )
            """;
        jdbc.execute(sql);
    }

    // Row mapper for BookRecord
    private BookRecord mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Timestamp returnTs = rs.getTimestamp("returndate");

        return new BookRecord(
            rs.getInt("loanid"),
            rs.getInt("copyid"),
            rs.getInt("userid"),
            rs.getTimestamp("checkoutdate").toLocalDateTime(),
            rs.getTimestamp("duedate").toLocalDateTime(),
            returnTs == null ? null : returnTs.toLocalDateTime(),
            rs.getInt("renewcount")
        );
    }

    // Find all records
    public List<BookRecord> findAll() {
        return jdbc.query("SELECT * FROM book_records ORDER BY loanid", this::mapRow);
    }

    // Find by loanID
    public BookRecord findById(int id) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM book_records WHERE loanid = ?",
                this::mapRow,
                id
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Find all loans by a user
    public List<BookRecord> findByUser(int userId) {
        return jdbc.query(
            "SELECT * FROM book_records WHERE userid = ? ORDER BY checkoutdate DESC",
            this::mapRow,
            userId
        );
    }

    // Find records for a particular copy
    public List<BookRecord> findByCopy(int copyId) {
        return jdbc.query(
            "SELECT * FROM book_records WHERE copyid = ? ORDER BY checkoutdate DESC",
            this::mapRow,
            copyId
        );
    }

    // Insert a new book record
    public int insert(BookRecord r) {
        Integer newId = jdbc.queryForObject(
            """
            INSERT INTO book_records (copyid, userid, checkoutdate, duedate, returndate, renewcount)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING loanid
            """,
            Integer.class,
            r.getCopyID(),
            r.getUserID(),
            Timestamp.valueOf(r.getCheckoutDate()),
            Timestamp.valueOf(r.getDueDate()),
            r.getReturnDate() == null ? null : Timestamp.valueOf(r.getReturnDate()),
            r.getRenewCount()
        );

        return newId;
    }

    // Update existing book record
    public int update(BookRecord r) {
        return jdbc.update(
            """
            UPDATE book_records
            SET copyid = ?, userid = ?, checkoutdate = ?, duedate = ?, returndate = ?, renewcount = ?
            WHERE loanid = ?
            """,
            r.getCopyID(),
            r.getUserID(),
            Timestamp.valueOf(r.getCheckoutDate()),
            Timestamp.valueOf(r.getDueDate()),
            r.getReturnDate() == null ? null : Timestamp.valueOf(r.getReturnDate()),
            r.getRenewCount(),
            r.getLoanID()
        );
    }

    // Delete by ID
    public int delete(int id) {
        return jdbc.update("DELETE FROM book_records WHERE loanid = ?", id);
    }
}
