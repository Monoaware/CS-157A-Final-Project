package edu.sjsu.library.models;

public class User {
    public int id;
    public String fname;
    public String lname;
    public String email;
    public String passwordhash;
    public String role;

    public User(int id, String fname, String lname, String email, String passwordhash, String role) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.passwordhash = passwordhash;
        this.role = role;
    }

    public int getId() { return id; }
    public String getFname() { return fname;}
    public String getLname() { return lname; }
    public String getEmail() { return email; }
    public String getPasswordhash() { return passwordhash; }
    public String getRole() { return role; }

    public void setId(int id) { this.id = id; }
    public void setFname(String fname) { this.fname = fname; }
    public void setLname(String lname) { this.lname = lname; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordhash(String passwordhash) { this.passwordhash = passwordhash; }
    public void setRole(String role) { this.role = role; }


    
}
