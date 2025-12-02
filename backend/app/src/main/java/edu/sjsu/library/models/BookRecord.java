package edu.sjsu.library.models;
import edu.sjsu.library.exceptions.BookAlreadyReturnedException;
import edu.sjsu.library.exceptions.RenewalNotAllowedException;
import java.time.LocalDateTime;

public class BookRecord {
    private int loanID;  // Primary key (auto-increment).
    private int copyID;
    private int userID;
    private LocalDateTime checkoutDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private int renewCount;

    private static final int LOAN_PERIOD_DAYS = 7;
    private static final int MAX_RENEWALS = 3;

    // Constructor for new book records (database will assign ID, checkoutDate is time of constructor call, dueDate is a week later).
    public BookRecord(int copyID, int userID) {
        this.copyID = copyID;
        this.userID = userID;
        this.checkoutDate = LocalDateTime.now();
        this.dueDate = this.checkoutDate.plusDays(LOAN_PERIOD_DAYS);
        this.returnDate = null;
        this.renewCount = 0;
    }
    
    // Constructor for existing records (loaded from database).
    public BookRecord(int loanID, int copyID, int userID, LocalDateTime checkoutDate, LocalDateTime dueDate, LocalDateTime returnDate, int renewCount) {
        this.loanID = loanID;
        this.copyID = copyID;
        this.userID = userID;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.renewCount = renewCount;
    }

    // Getters:
    public int getLoanID() { return loanID; }
    public int getCopyID() { return copyID; }
    public int getUserID() { return userID; }
    public LocalDateTime getCheckoutDate() { return checkoutDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public int getRenewCount() { return renewCount; }
    public static int getMaxRenewals() { 
        return MAX_RENEWALS; 
    }
    public static int getLoanPeriodDays() { 
        return LOAN_PERIOD_DAYS; 
    }
    
    // Setters:
    public void setCopyID(int copyID) { this.copyID = copyID; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setCheckoutDate(LocalDateTime checkoutDate) { this.checkoutDate = checkoutDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public void setRenewCount(int renewCount) { this.renewCount = renewCount; }

    // Helper methods:
    private void assertRenewable() {
        if (this.getReturnDate() != null) {
            throw new RenewalNotAllowedException("Cannot renew: book has already been returned on " + this.getReturnDate());
        }
        if (this.getRenewCount() >= MAX_RENEWALS) {
            throw new RenewalNotAllowedException("Cannot renew: max renewals reached.");
        }
    }

    // Staff can override normal renewal limits.
    public boolean staffRenew() {
        // Only check if already returned - skip max renewal check
        if (this.getReturnDate() != null) {
            throw new RenewalNotAllowedException("Cannot renew: book has already been returned on " + this.getReturnDate());
        }
        
        this.renewCount++;
        this.dueDate = this.dueDate.plusDays(LOAN_PERIOD_DAYS);
        return true;
    }

    // Public methods:
    // Call this to actually update BookRecord w/ renewal information.
    public boolean renew() {
        assertRenewable();
        this.renewCount++;
        this.dueDate = this.dueDate.plusDays(LOAN_PERIOD_DAYS);
        return true;
    }

    // Call this method whenever a book is returned.
    public boolean returnBook() {
        if (this.returnDate != null) {
            throw new BookAlreadyReturnedException("Book already returned at " + this.getReturnDate());
        } 
        this.returnDate = LocalDateTime.now();
        return true;
    }
}