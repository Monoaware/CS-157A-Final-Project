package edu.sjsu.library;

import edu.sjsu.library.dao.*;
import edu.sjsu.library.models.*;
import edu.sjsu.library.utils.DBConnection;

import java.sql.*;
import java.util.List;

public class DAOTests {

    public static void main(String[] args) {
        // testUserDAO();

        testTitleDAO();
    }


    public static void testUserDAO() {

        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            userDAO.createTable();

            // Test 1: Add a new user
            User newUser = new User("John", "Doe", "johndoe@example.com", "hashedpass", "student");
            System.out.println(newUser.getFname());
            userDAO.addUser(newUser);
            System.out.println("✅ Added user successfully.");

            // Test 2: Retrieve user by ID
            User retrieved = userDAO.getUserById(1);
            if (retrieved != null) {
                System.out.println("✅ Retrieved user: " + retrieved.getFname() + retrieved.getLname());
            } else {
                System.out.println("❌ User not found.");
            }

            // Test 3: Get all users
            List<User> allUsers = userDAO.getAllUsers();
            System.out.println("✅ Found " + allUsers.size() + " user(s).");

            // Test 4: Delete user
            userDAO.deleteUser(1);
            System.out.println("✅ Deleted user with ID 1.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void testTitleDAO() {
        try (Connection conn = DBConnection.getConnection()) {
            TitleDAO titleDAO = new TitleDAO(conn);
            titleDAO.createTable();

            // Test 1: Add a new title
            Title newTitle = new Title("1234567890", "Sample Book", "Jane Smith", 2023, "Fiction", true);
            titleDAO.addTitle(newTitle);
            System.out.println("✅ Added title successfully.");

            // Test 2: Retrieve title by ID
            Title retrieved = titleDAO.getTitleById(1);
            if (retrieved != null) {
                System.out.println("✅ Retrieved title: " + retrieved.getTitle());
            } else {
                System.out.println("❌ Title not found.");
            }

            // Test 3: Get all titles
            List<Title> allTitles = titleDAO.getAllTitles();
            System.out.println("✅ Found " + allTitles.size() + " title(s).");

            // Test 4: Delete title
            titleDAO.deleteTitle(1);
            System.out.println("✅ Deleted title with ID 1.");
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }
}