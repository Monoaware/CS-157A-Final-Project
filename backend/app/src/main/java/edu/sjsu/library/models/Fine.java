package edu.sjsu.library.models;

public class Fine {
    private int userID;
    private int loanID;
    private double amount;
    private String fineDate;
    private String reason;
    private String status;

    public Fine(int userID, int loanID, double amount, String fineDate, String reason, String status) {
        this.userID = userID;
        this.loanID = loanID;
        this.amount = amount;
        this.fineDate = fineDate;
        this.reason = reason;
        this.status = status;
    }

    public int getUserID() { return userID; }
    public int getLoanID() { return loanID; }
    public double getAmount() { return amount; }
    public String getFineDate() { return fineDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }

    public void setUserID(int userID) { this.userID = userID; }
    public void setLoanID(int loanID) { this.loanID = loanID; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setFineDate(String fineDate) { this.fineDate = fineDate; }
    public void setReason(String reason) { this.reason = reason; }
    public void setStatus(String status) { this.status = status; }
}
