package edu.sjsu.library.dao;

import edu.sjsu.library.models.Title;
import edu.sjsu.library.models.Title.Genre;
import java.time.Year;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public class TitleDAO {

    private final JdbcTemplate jdbc;

    public TitleDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS titles (
                id SERIAL PRIMARY KEY,
                ISBN VARCHAR(255) NOT NULL,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL,
                yearPublished INT NOT NULL,
                genre VARCHAR(100),
                isVisible BOOLEAN DEFAULT TRUE
            )
            """;
        jdbc.execute(sql);
    }

    public List<Title> findAll() {
        return jdbc.query(
            "SELECT id, isbn, title, author, yearpublished, genre, isvisible FROM titles ORDER BY id",
            (rs, n) -> new Title(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                Year.of(rs.getInt("yearpublished")),
                Genre.valueOf(rs.getString("genre").trim().toUpperCase()),
                rs.getBoolean("isvisible")
            )
        );
    }

    public Title findById(int id) {
        try {
            return jdbc.queryForObject(
                "SELECT id, isbn, title, author, yearpublished, genre, isvisible FROM titles WHERE id = ?",
                (rs, n) -> new Title(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    Year.of(rs.getInt("yearpublished")),
                    Genre.valueOf(rs.getString("genre").trim().toUpperCase()),
                    rs.getBoolean("isvisible")
                ),
                id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Title findByIsbn(String isbn) {
        try {
            return jdbc.queryForObject(
                "SELECT id, isbn, title, author, yearpublished, genre, isvisible FROM titles WHERE isbn = ?",
                (rs, n) -> new Title(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    Year.of(rs.getInt("yearpublished")),
                    Genre.valueOf(rs.getString("genre").trim().toUpperCase()),
                    rs.getBoolean("isvisible")
                ),
                isbn
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Title> findByYearRange(Year startYear, Year endYear) {
        return jdbc.query(
            "SELECT id, isbn, title, author, yearpublished, genre, isvisible FROM titles WHERE yearpublished BETWEEN ? AND ? ORDER BY yearpublished DESC, title",
            (rs, n) -> new Title(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                Year.of(rs.getInt("yearpublished")),
                Genre.valueOf(rs.getString("genre").trim().toUpperCase()),
                rs.getBoolean("isvisible")
            ),
            startYear.getValue(), endYear.getValue()
        );
    }

    public List<Title> findByAuthor(String author) {
        return jdbc.query(
            "SELECT id, isbn, title, author, yearpublished, genre, isvisible FROM titles WHERE LOWER(author) LIKE LOWER(?) ORDER BY title",
            (rs, n) -> new Title(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                Year.of(rs.getInt("yearpublished")),
                Genre.valueOf(rs.getString("genre").trim().toUpperCase()),
                rs.getBoolean("isvisible")
            ),
            "%" + author + "%"
        );
    }

    public List<Title> findByGenre(Genre genre) {
        return jdbc.query(
            "SELECT id, isbn, title, author, yearpublished, genre, isvisible FROM titles WHERE genre = ? ORDER BY title",
            (rs, n) -> new Title(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                Year.of(rs.getInt("yearpublished")),
                Genre.valueOf(rs.getString("genre").trim().toUpperCase()),
                rs.getBoolean("isvisible")
            ),
            genre.toString()
        );
    }



    /** Insert and return generated id */
    public int insert(Title t) {
        String sql = """
            INSERT INTO titles (isbn, title, author, yearpublished, genre, isvisible)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        Integer newId = jdbc.queryForObject(
            sql, Integer.class,
            t.getISBN(),
            t.getTitle(),
            t.getAuthor(),
            t.getYearPublished(),
            t.getGenre(),
            t.isVisible()
        );
        return newId;
    }

    public int update(Title t) {
        String sql = """
            UPDATE titles
               SET isbn = ?, title = ?, author = ?, yearpublished = ?, genre = ?, isvisible = ?
             WHERE id = ?
            """;
        return jdbc.update(
            sql,
            t.getISBN(),
            t.getTitle(),
            t.getAuthor(),
            t.getYearPublished(),
            t.getGenre(),
            t.isVisible(),
            t.getTitleID()              
        );
    }

    public List<Title> findAllVisible() {
        return jdbc.query(
            "SELECT id, isbn, title, author, yearpublished, genre, isvisible FROM titles WHERE isvisible = TRUE ORDER BY id",
            (rs, n) -> new Title(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                Year.of(rs.getInt("yearpublished")),
                Genre.valueOf(rs.getString("genre").trim().toUpperCase()),
                rs.getBoolean("isvisible")
            )
        );
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM titles WHERE id = ?", id);
    }
}