package edu.sjsu.library;

import edu.sjsu.library.utils.DBConnection;
import java.sql.Connection;

public class App {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✅ Connected to PostgreSQL successfully!");
        } catch (Exception e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }
    }
}