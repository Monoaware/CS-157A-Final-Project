package edu.sjsu.library.models;

public class Copy {
    private int copyID; // Primary key (auto-increment).
    private int titleID;
    private String barcode;
    public enum CopyStatus {
        AVAILABLE, 
        CHECKED_OUT,
        LOST,
        DAMAGED,
        MAINTENANCE, // Book copy is under repair or being catalogued.
        RESERVED
    };
    private CopyStatus status;
    private String location;
    private boolean isVisible;
    
    // Constructor for new book copies (database will assign ID, and visibility is true by default).
    public Copy(int titleID, String barcode, CopyStatus status, String location) {
        this.titleID = titleID;
        this.barcode = barcode; 
        this.status = status;
        this.location = location;
        this.isVisible = true;
    }

    // Constructor for existing book copies (loaded from database).
    public Copy(int copyID, int titleID, String barcode, CopyStatus status, String location, boolean isVisible) {
        this.copyID = copyID;
        this.titleID = titleID;
        this.barcode = barcode;
        this.status = status;
        this.location = location;
        this.isVisible = isVisible;
    }

    // Getters:
    public int getCopyID() { return copyID; }
    public int getTitleID() { return titleID; }
    public String getBarcode() { return barcode; }
    public CopyStatus getStatus() { return status; }
    public String getLocation() { return location; }
    public boolean isVisible() { return isVisible; }

    // Setters:
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setLocation(String location) { this.location = location; }
    public void setVisible(boolean visible) { isVisible = visible; }
    public void setTitleID(int titleID) { this.titleID = titleID; }
    public void setStatus(CopyStatus status) { this.status = status; }

    // Public methods:
    public boolean isAvailable() { return this.status == CopyStatus.AVAILABLE; }
    public boolean isCheckedOut() { return this.status == CopyStatus.CHECKED_OUT; }
    public boolean isReserved() { return this.status == CopyStatus.RESERVED; }
    
    public void markAvailable() { this.status = CopyStatus.AVAILABLE; }
    public void markCheckedOut() { this.status = CopyStatus.CHECKED_OUT; }
    public void markLost() { this.status = CopyStatus.LOST; }
    public void markDamaged() { this.status = CopyStatus.DAMAGED; }
    public void markMaintenance() { this.status = CopyStatus.MAINTENANCE; }
    public void markReserved() { this.status = CopyStatus.RESERVED; }
}