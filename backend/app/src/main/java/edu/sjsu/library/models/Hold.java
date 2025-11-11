package edu.sjsu.library.models;

public class Hold {
    private int userID;
    private int titleID;
    private int copyID;
    private String status;
    private String placedAt;
    private String readyAt;
    private String pickupExpire;
    private int position;
    
    public Hold(int userID, int titleID, int copyID, String status, String placedAt, String readyAt, String pickupExpire, int position) {
        this.userID = userID;
        this.titleID = titleID;
        this.copyID = copyID;
        this.status = status;
        this.placedAt = placedAt;
        this.readyAt = readyAt;
        this.pickupExpire = pickupExpire;
        this.position = position;
    }

    public int getUserID() { return userID;}
    public int getTitleID() { return titleID; }
    public int getCopyID() { return copyID; }
    public String getStatus() { return status; }
    public String getPlacedAt() { return placedAt; }
    public String getReadyAt() { return readyAt; }
    public String getPickupExpire() { return pickupExpire; }
    public int getPosition() { return position; }

    public void setUserID(int userID) { this.userID = userID; }
    public void setTitleID(int titleID) { this.titleID = titleID; }
    public void setCopyID(int copyID) { this.copyID = copyID; }
    public void setStatus(String status) { this.status = status; }
    public void setPlacedAt(String placedAt) { this.placedAt = placedAt;}
    public void setReadyAt(String readyAt) { this.readyAt = readyAt; }
    public void setPickupExpire(String pickupExpire) { this.pickupExpire = pickupExpire; }
    public void setPosition(int position) { this.position = position; }
}