package edu.sjsu.library.models;
import java.time.LocalDateTime;

import edu.sjsu.library.exceptions.HoldChangeNotAllowedException;

public class Hold {
    private int holdID;
    private int userID;
    private int titleID;
    private int copyID;
    public enum HoldStatus {
        QUEUED,
        READY,
        PICKED_UP,
        CANCELLED,
        EXPIRED
    };
    private HoldStatus status;
    private LocalDateTime placedAt;
    private LocalDateTime readyAt;
    private LocalDateTime pickupExpire;
    private int position; // Position is based on a FIFO queue where 1 is the very beginning of the queue (first to be served).

    private static final int PICKUP_WINDOW_DAYS = 3; // Members have 3 days to pickup their copy after it's ready.
    
    // Constructor for new hold requests (database will assign ID, default status is QUEUED, default placedAt is time of constructor call, default readyAt & pickupExpire are NULL).
    public Hold(int userID, int titleID, int copyID, int position) {
        this.userID = userID;
        this.titleID = titleID;
        this.copyID = copyID;
        this.status = HoldStatus.QUEUED;
        this.placedAt = LocalDateTime.now();
        this.readyAt = null;
        this.pickupExpire = null;
        this.position = position;
    }

    // Constructor for existing hold requests (loaded from database).
    public Hold(int holdID, int userID, int titleID, int copyID, HoldStatus status, LocalDateTime placedAt, LocalDateTime readyAt, LocalDateTime pickupExpire, int position) {
        this.holdID = holdID;
        this.userID = userID;
        this.titleID = titleID;
        this.copyID = copyID;
        this.status = status;
        this.placedAt = placedAt;
        this.readyAt = readyAt;
        this.pickupExpire = pickupExpire;
        this.position = position;
    }

    // Getters:
    public int getUserID() { return userID;}
    public int getTitleID() { return titleID; }
    public int getCopyID() { return copyID; }
    public int getHoldID() { return holdID; }
    public HoldStatus getStatus() { return status; }
    public LocalDateTime getPlacedAt() { return placedAt; }
    public LocalDateTime getReadyAt() { return readyAt; }
    public LocalDateTime getPickupExpire() { return pickupExpire; }
    public int getPosition() { return position; }

    // Helper methods:
    private void assertCanMarkReady() {
        if (this.status != HoldStatus.QUEUED) {
            throw new HoldChangeNotAllowedException("Hold ready error: cannot mark as ready when status is " + this.status);
        }
    }

    private void assertCanMarkPickedUp() {
        if (this.status != HoldStatus.READY) {
            throw new HoldChangeNotAllowedException("Hold pick-up error: cannot mark as picked up when status is " + this.status);
        }
    }

    private void assertCanMarkCancelled() {
        if (this.status == HoldStatus.PICKED_UP || this.status == HoldStatus.EXPIRED) {
            throw new HoldChangeNotAllowedException("Hold cancellation error: cannot mark as cancelled when status is " + this.status);
        }
    }

    private void assertCanMarkExpired() {
        if (!this.pickupExpire.isBefore(LocalDateTime.now())) {
            throw new HoldChangeNotAllowedException("Hold expiration error: cannot mark as expired when pickup expiration time has not yet passed.");
        }
        if (this.status != HoldStatus.READY) {
            throw new HoldChangeNotAllowedException("Hold expiration error: cannot mark as expired when status is " + this.status);
        }
    }

    // Public methods:
    public void markReady(LocalDateTime readyAt) {
        assertCanMarkReady();
        this.status = HoldStatus.READY;
        this.readyAt = readyAt;
        this.pickupExpire = readyAt.plusDays(PICKUP_WINDOW_DAYS);
    }

    public void markPickedUp() {
        assertCanMarkPickedUp();
        this.status = HoldStatus.PICKED_UP;
    }

    public void markCancelled() {
        assertCanMarkCancelled();
        this.status = HoldStatus.CANCELLED;
        this.pickupExpire = null;
    }

    public void markExpired() {
        assertCanMarkExpired();
        this.status = HoldStatus.EXPIRED;
    }

    public boolean decrementPosition() {
        if (this.position <= 1) {
            throw new IllegalStateException("Cannot decrement position: position cannot be less than 1.");
        }
        this.position--;
        return true;
    }
}