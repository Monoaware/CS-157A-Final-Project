/* 
    This is a small model to help with summarizing book statuses.
*/
package edu.sjsu.library.models;

public class AvailabilitySummary {
    private int totalCopies;
    private int availableCopies;
    private int checkedOutCopies;
    private int otherStatusCopies; // Lost, damaged, maintenance, reserved.
    
    public AvailabilitySummary(int totalCopies, int availableCopies, int checkedOutCopies, int otherStatusCopies) {
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.checkedOutCopies = checkedOutCopies;
        this.otherStatusCopies = otherStatusCopies;
    }
    
    // Getters.
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public int getCheckedOutCopies() { return checkedOutCopies; }
    public int getOtherStatusCopies() { return otherStatusCopies; }
    public boolean isAvailable() { return availableCopies > 0; }
}