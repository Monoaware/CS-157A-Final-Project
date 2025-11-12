// FILE IS NOT NEEDED ANYMORE BUT SAVING SO CODE DOESN"T BREAK

package edu.sjsu.library.utils;

import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        try{
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/librarydb?sslmode=disable", "postgres", "postgres");

        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }  
}
