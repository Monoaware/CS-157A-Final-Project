package edu.sjsu.library.models;
import edu.sjsu.library.exceptions.UserStatusChangeNotAllowedException;

public class User {
    private int userID;
    private String fname;
    private String lname;
    private String email;
    private String passwordhash;
    private enum UserRole {
        MEMBER,
        STAFF
    };
    private UserRole role;
    private enum UserStatus {
        ACTIVE,
        INACTIVE,
        RESTRICTED
    };
    private UserStatus status;

    // Constructor for new user (database auto-increments, default UserStatus is ACTIVE).
    public User(String fname, String lname, String email, String passwordhash, UserRole role) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.passwordhash = passwordhash;
        this.role = role;
        this.status = UserStatus.ACTIVE;
    }

    // Constructor for existing user (loads from database).
    public User(int userID, String fname, String lname, String email, String passwordhash, UserRole role, UserStatus status) {
        this.userID = userID;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.passwordhash = passwordhash;
        this.role = role;
        this.status = status;
    }

    // Getters:
    public int getUserID() { return userID; }
    public String getFname() { return fname; }
    public String getLname() { return lname; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordhash; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }

    // Setters:
    public void setFname(String fname) { this.fname = fname; }
    public void setLname(String lname) { this.lname = lname; }
    public void setEmail(String email) { this.email = email; }
    private void setPasswordHash(String passwordhash) { this.passwordhash = passwordhash; }

    // Public methods:
    public boolean isActive() { return this.status == UserStatus.ACTIVE; }
    public boolean isRestricted() { return this.status == UserStatus.RESTRICTED; }
    public boolean isStaff() { return this.role == UserRole.STAFF; }
    public boolean isMember() { return this.role == UserRole.MEMBER; }

    public boolean activate() { 
        if (this.status == UserStatus.ACTIVE) {
            throw new UserStatusChangeNotAllowedException("Cannot activate user when already active.");
        }
        this.status = UserStatus.ACTIVE; 
        return true;
    }

    public boolean deactivate() { 
        if (this.status == UserStatus.INACTIVE) {
            throw new UserStatusChangeNotAllowedException("Cannot deactivate user when already inactive.");
        }
        this.status = UserStatus.INACTIVE; 
        return true;
    }

    public boolean restrict() { 
        if (this.status == UserStatus.RESTRICT) {
            throw new UserStatusChangeNotAllowedException("Cannot restrict user when already restricted.");
        }
        this.status = UserStatus.RESTRICTED; 
        return true;
    }
}
