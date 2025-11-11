package edu.sjsu.library.models;

public class Copy {
    private int titleID;
    private String barcode;
    private String status;
    private String location;
    private boolean isVisible;
    
    public Copy(int titleID, String barcode, String status, String location, boolean isVisible) {
        this.titleID = titleID;
        this.barcode = barcode;
        this.status = status;
        this.location = location;
        this.status = status;
    }

    public int getTitleID() { return titleID;}
    public String getBarcode() { return barcode; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
    public boolean isVisible() { return isVisible; }

    public void setTitleID(int titleID) { this.titleID = titleID; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setStatus(String status) { this.status = status; }
    public void setLocation(String location) { this.location = location; }
    public void setVisible(boolean visible) { isVisible = visible; }
}
