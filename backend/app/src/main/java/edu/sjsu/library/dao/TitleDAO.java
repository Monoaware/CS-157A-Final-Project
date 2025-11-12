package edu.sjsu.library.dao;

import edu.sjsu.library.models.Title;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TitleDAO {
    private final Connection connection;

    public TitleDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS titles (" +
                     "id SERIAL PRIMARY KEY, " +
                     "ISBN VARCHAR(255) NOT NULL, " +
                     "title VARCHAR(255) NOT NULL, " +
                     "author VARCHAR(255) NOT NULL, " +
                     "yearPublished INT NOT NULL," +
                     "genre VARCHAR(100), " +
                     "isVisible BOOLEAN DEFAULT TRUE" +
                     ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
        }
    }

    public void addTitle(Title title) throws SQLException {
        String query = "INSERT INTO titles (ISBN, title, author, yearPublished, genre, isVisible) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, title.getISBN());
            stmt.setString(2, title.getTitle());
            stmt.setString(3, title.getAuthor());
            stmt.setInt(4, title.getYearPublished());
            stmt.setString(5, title.getGenre());
            stmt.setBoolean(6, title.isVisible());
            stmt.executeUpdate();
        }
    }

    public Title getTitleById(int id) throws SQLException {
        String query = "SELECT * FROM titles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Title(
                    rs.getInt("id"),
                    rs.getString("ISBN"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("yearPublished"),
                    rs.getString("genre"),
                    rs.getBoolean("isVisible")
                );
            }
        }
        return null;
    }

    public List<Title> getAllTitles() throws SQLException {
        List<Title> titles = new ArrayList<>();
        String query = "SELECT * FROM titles";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Title title = new Title(
                    rs.getInt("id"),
                    rs.getString("ISBN"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("yearPublished"),
                    rs.getString("genre"),
                    rs.getBoolean("isVisible")
                );
                titles.add(title);
            }
        }
        return titles;
    }   

    public void deleteTitle(int id) throws SQLException {
        String query = "DELETE FROM titles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();   
        }
    }
}   
