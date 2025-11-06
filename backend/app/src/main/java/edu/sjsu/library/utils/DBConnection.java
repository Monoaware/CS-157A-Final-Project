package edu.sjsu.library.utils;

import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        try{
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/librarydb", "postgres", "postgres");

        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }  
}
