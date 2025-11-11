package edu.sjsu.library.models;

public class BookRecord {
    private int copyID;
    private int userID;
    private String checkoutDate;
    private String dueDate;
    private String returnDate;
    private int renewCount;

    public BookRecord(int copyID, int userID, String checkoutDate, String dueDate, String returnDate, int renewCount) {
        this.copyID = copyID;
        this.userID = userID;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.renewCount = renewCount;
    }

    public int getCopyID() { return copyID; }
    public int getUserID() { return userID; }
    public String getCheckoutDate() { return checkoutDate; }
    public String getDueDate() { return dueDate; }
    public String getReturnDate() { return returnDate; }
    public int getRenewCount() { return renewCount; }

    public void setCopyID(int copyID) { this.copyID = copyID; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setCheckoutDate(String checkoutDate) { this.checkoutDate = checkoutDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate;}
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public void setRenewCount(int renewCount) { this.renewCount = renewCount;}
    
}
