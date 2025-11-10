package edu.sjsu.library.models;

public class User {
    public String fname;
    public String lname;
    public String email;
    public String passwordhash;
    public String role;


    public User(String fname, String lname, String email, String passwordhash, String role) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.passwordhash = passwordhash;
        this.role = role;
    }

    public String getFname() {
        return fname;
    }

    public String setFname(String fname) {
        this.fname = fname;
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String setLname(String lname) {
        this.lname = lname;
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public String setEmail(String email) {
        this.email = email;
        return email;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public String setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
        return passwordhash;
    }

    public String getRole() {
        return role;
    }

    public String setRole(String role) {
        this.role = role;
        return role;
    }
}
