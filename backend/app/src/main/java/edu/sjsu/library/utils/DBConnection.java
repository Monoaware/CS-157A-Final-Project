// FILE IS NOT NEEDED ANYMORE BUT SAVING SO CODE DOESN"T BREAK

package edu.sjsu.library.utils;

import java.sql.*;

// We need to throw the exception for it to print out in App.java!
public class DBConnection {
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/librarydb?sslmode=disable", "postgres", "postgres");
        }
    }  
}
