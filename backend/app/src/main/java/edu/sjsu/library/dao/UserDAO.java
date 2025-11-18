package edu.sjsu.library.dao;

import edu.sjsu.library.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
                role VARCHAR(20) NOT NULL, 
                status VARCHAR(20) NOT NULL
            )   
            """;
        jdbc.execute(sql);
    }

    public List<User> findAll() {
        return jdbc.query(
            "SELECT id, fname, lname, email, passwordhash, role, status FROM users ORDER BY id",
            (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("fname"),
                rs.getString("lname"),
                rs.getString("email"),
                rs.getString("passwordhash"),
                User.UserRole.valueOf(rs.getString("role").trim().toUpperCase()),
                User.UserStatus.valueOf(rs.getString("status").trim().toUpperCase())
            )
        );
    }

    public User findById(int id) {
        try {
            return jdbc.queryForObject(
                "SELECT id, fname, lname, email, passwordhash, role, status FROM users WHERE id = ?",
                (rs, rowNum) -> new User(
                    rs.getInt("id"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("email"),
                    rs.getString("passwordhash"),
                    User.UserRole.valueOf(rs.getString("role").trim().toUpperCase()),
                    User.UserStatus.valueOf(rs.getString("status").trim().toUpperCase())
                ),
                id
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User findByEmail(String email) {
        try {
            return jdbc.queryForObject(
                "SELECT id, fname, lname, email, passwordhash, role, status FROM users WHERE email = ?",
                (rs, rowNum) -> new User(
                    rs.getInt("id"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("email"),
                    rs.getString("passwordhash"),
                    User.UserRole.valueOf(rs.getString("role").trim().toUpperCase()),
                    User.UserStatus.valueOf(rs.getString("status").trim().toUpperCase())
                ),
                email
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

     public int insert(User u) {
         Integer newId = jdbc.queryForObject(
             """
             INSERT INTO users (fname, lname, email, passwordhash, role)
             VALUES (?, ?, ?, ?, ?)
             RETURNING id
             """,
             Integer.class,
             u.getFname(), u.getLname(), u.getEmail(),
             u.getPasswordHash(), u.getRole().name(), u.getStatus().name()
         );
         return newId;
     }

    public int update(User u) {
        return jdbc.update(
            "UPDATE users SET fname = ?, lname = ?, email = ?, passwordhash = ?, role = ?, status = ? WHERE id = ?",
            u.getFname(), u.getLname(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.getStatus().name(), u.getUserID()
        );
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM users WHERE id = ?", id);
    }
}