package edu.sjsu.library.dao;

import edu.sjsu.library.models.User;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbc;

    public UserDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                fname VARCHAR(50) NOT NULL,
                lname VARCHAR(50) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                passwordhash VARCHAR(255) NOT NULL,
                role VARCHAR(20) NOT NULL
            )
            """;
        jdbc.execute(sql);
    }

        // Row mapper for query results
    private static final RowMapper<User> USER_MAPPER = (rs, n) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFname(rs.getString("fname"));
        u.setLname(rs.getString("lname"));
        u.setEmail(rs.getString("email"));
        u.setPasswordhash(rs.getString("passwordhash"));
        u.setRole(rs.getString("role"));
        return u;
    };

    public List<User> findAll() {
        return jdbc.query(
            "SELECT id, fname, lname, email, passwordhash, role FROM users ORDER BY id",
            USER_MAPPER
        );
    }

    public User findById(int id) {
        try {
            return jdbc.queryForObject(
                "SELECT id, fname, lname, email, passwordhash, role FROM users WHERE id = ?",
                USER_MAPPER, id
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int insert(User u) {
        return jdbc.update(
            "INSERT INTO users (fname, lname, email, passwordhash, role) VALUES (?, ?, ?, ?, ?)",
            u.getFname(), u.getLname(), u.getEmail(), u.getPasswordhash(), u.getRole()
        );
    }

    public int update(User u) {
        return jdbc.update(
            "UPDATE users SET fname = ?, lname = ?, email = ?, passwordhash = ?, role = ? WHERE id = ?",
            u.getFname(), u.getLname(), u.getEmail(), u.getPasswordhash(), u.getId(), u.getRole()
        );
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM users WHERE id = ?", id);
    }
}