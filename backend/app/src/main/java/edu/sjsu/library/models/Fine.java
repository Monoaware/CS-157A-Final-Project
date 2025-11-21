package edu.sjsu.library.models;
import edu.sjsu.library.exceptions.FinePaymentNotAllowedException;
import edu.sjsu.library.exceptions.FineWaivementNotAllowedException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Note: Fines must be paid in full or not at all to simplify transactions.
public class Fine {
    private int fineID; // Primary key (auto-increment).
    private int userID;
    private int loanID;
    private BigDecimal amount; // Don't use double (potential rounding errors).
    private LocalDateTime fineDate; 
    private String reason;
    public enum FineStatus {
        UNPAID,
        PAID, 
        WAIVED
    };
    private FineStatus status;

    // Constructor for new fines (database will assign ID, fineDate is the same as constructor call time, status is UNPAID by default).
    public Fine(int userID, int loanID, BigDecimal amount, String reason) {
        this.userID = userID;
        this.loanID = loanID;
        this.amount = amount;
        this.fineDate = LocalDateTime.now();
        this.reason = reason;
        this.status = FineStatus.UNPAID;
    }

    // Constructor for existing fines (loaded from database).
    public Fine(int fineID, int loanID, BigDecimal amount, LocalDateTime fineDate, String reason, FineStatus status) {
        this.fineID = fineID;
        this.loanID = loanID;
        this.amount = amount;
        this.fineDate = fineDate;
        this.reason = reason;
        this.status = status;
    }

    // Getters:
    public int getFineID() { return fineID; }
    public int getUserID() { return userID; }
    public int getLoanID() { return loanID; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getFineDate() { return fineDate; }
    public String getReason() { return reason; }
    public FineStatus getStatus() { return status; }

    // Setters:
    public void setReason(String reason) { this.reason = reason; }
    private void setStatus(FineStatus status) { this.status = status; }

    // Helper methods:
    private void assertPayable() {
        if (this.status == FineStatus.PAID) {
            throw new FinePaymentNotAllowedException("Payment failed: your fine has already been paid.");
        }
        if (this.status == FineStatus.WAIVED) {
            throw new FinePaymentNotAllowedException("Payment failed: your fine has already been waived.");
        }
    }

    private void assertWaivable() {
        if (this.status == FineStatus.PAID) {
            throw new FineWaivementNotAllowedException("Waivement failed: the fine has already been paid.");
        }
        if (this.status == FineStatus.WAIVED) {
            throw new FineWaivementNotAllowedException("Waivement failed: the fine has already been waived.");
        }
    }

    // Public methods:
    public boolean payFine() {
        assertPayable();
        this.setStatus(FineStatus.PAID);
        return true;
    }

    public boolean waiveFine() {
        assertWaivable();
        this.setStatus(FineStatus.WAIVED);
        return true;
    }
}
