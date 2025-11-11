package edu.sjsu.library;

import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.User;
import edu.sjsu.library.utils.DBConnection;

import java.sql.*;
import java.util.List;

public class DAOTests {

    public static void main(String[] args) {
        testUserDAO();
    }


    public static void testUserDAO() {

        try (Connection conn = DBConnection.getConnection()) {
                UserDAO userDAO = new UserDAO(conn);
                userDAO.createTable();

                // Test 1: Add a new user
                User newUser = new User(0, "John", "Doe", "johndoe@example.com", "hashedpass", "student");
                System.out.println(newUser.getFname());
                userDAO.addUser(newUser);
                System.out.println("✅ Added user successfully.");

                // Test 2: Retrieve user by ID
                User retrieved = userDAO.getUserById(0);
                if (retrieved != null) {
                    System.out.println("✅ Retrieved user: " + retrieved.getFname() + retrieved.getLname());
                } else {
                    System.out.println("❌ User not found.");
                }

                // Test 3: Get all users
                List<User> allUsers = userDAO.getAllUsers();
                System.out.println("✅ Found " + allUsers.size() + " user(s).");

                // Test 4: Delete user
                userDAO.deleteUser(0);
                System.out.println("✅ Deleted user with ID 0.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}