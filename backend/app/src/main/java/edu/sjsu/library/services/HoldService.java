/* 
    HoldService.java handles the hold operation specifically since it is slightly more complex in nature.
*/
package edu.sjsu.library.services;

import edu.sjsu.library.dao.HoldDAO;
import edu.sjsu.library.dao.TitleDAO;
import edu.sjsu.library.dao.CopyDAO;
import edu.sjsu.library.models.Hold;
import edu.sjsu.library.models.Title;
import edu.sjsu.library.models.Copy;
import edu.sjsu.library.models.User;
import edu.sjsu.library.utils.AuthorizationUtils;
import edu.sjsu.library.exceptions.AuthenticationFailedException;
import edu.sjsu.library.exceptions.AuthorizationFailedException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HoldService {
    
    private final HoldDAO holdDAO;
    private final TitleDAO titleDAO;
    private final CopyDAO copyDAO;
    private final AuthorizationUtils authUtils;
    
    // Constructor:
    public HoldService(HoldDAO holdDAO, TitleDAO titleDAO, CopyDAO copyDAO, AuthorizationUtils authUtils) {
        this.holdDAO = holdDAO;
        this.titleDAO = titleDAO;
        this.copyDAO = copyDAO;
        this.authUtils = authUtils;
    }
    
    // Helper methods:
    // Reorder hold queue after cancellation or expiration.
    private void reorderHoldQueue(int titleID) {
        
        // Grab all active holds for a title and order by position.
        List<Hold> holds = holdDAO.findByTitleOrderedByPosition(titleID);
        
        // Iterate over the holds in ascending order while assigning new consecutive positions. 
        for (int i = 0; i < holds.size(); i++) {
            Hold hold = holds.get(i);
            int newPosition = i + 1;
            
            if (hold.getPosition() != newPosition) {
                hold.setPosition(newPosition);
                holdDAO.update(hold);
            }
        }
    }

    // 1. Place a hold on a title (MEMBERS only - staff don't need holds).
    public Hold placeHold(int titleID, int requestorID) {

        // Verify user authentication (members only).
        User requestor = authUtils.getRequestor(requestorID);
        if (requestor.isStaff()) {
            throw new AuthorizationFailedException("Staff members cannot place holds.");
        }
        
        // Verify title exists and is visible to member.
        Title title = titleDAO.findById(titleID);
        if (title == null || !title.isVisible()) {
            throw new IllegalArgumentException("Title not found or not available for holds.");
        }
        
        // Check if user already has a hold on this title (don't want a user to place hold on same title multiple times).
        if (holdDAO.findActiveHoldByUserAndTitle(requestorID, titleID) != null) {
            throw new IllegalArgumentException("You already have an active hold on this title.");
        }
        
        // Check if there are available copies (if yes, they should check out instead).
        List<Copy> availableCopies = copyDAO.findAvailableByTitle(titleID);
        if (!availableCopies.isEmpty()) {
            throw new IllegalArgumentException("Copies are currently available - please check out directly instead of placing a hold.");
        }
        
        // Create the hold with next position in queue.
        int position = holdDAO.getNextPosition(titleID);
        Hold newHold = new Hold(requestorID, titleID, null, position);
        
        // Insert and return with generated ID.
        int holdID = holdDAO.insert(newHold);
        return holdDAO.findById(holdID);
    }
    
    // 2. Cancel a hold (MEMBERS can cancel their own, STAFF can cancel any).
    public boolean cancelHold(int holdID, int requestorID) {

        // Verify that the user exists and get their details.
        User requestor = authUtils.getRequestor(requestorID);
        
        // Find the hold.
        Hold hold = holdDAO.findById(holdID);
        if (hold == null) {
            throw new IllegalArgumentException("Hold not found with ID: " + holdID);
        }
        
        // Check permissions.
        if (!requestor.isStaff() && hold.getUserID() != requestorID) {
            throw new AuthorizationFailedException("You can only cancel your own holds.");
        }
        
        // Cancel the hold (HoldDAO.java delete() returns an int).
        int rowsAffected = holdDAO.delete(holdID);
        boolean success = rowsAffected > 0;
        
        if (success) {
            // Reorder the queue for this title.
            reorderHoldQueue(hold.getTitleID());
        }
        
        return success;
    }
    
    // 3. Process next hold when a copy becomes available (STAFF only).
    public Hold processNextHold(int titleID, int copyID, int requestorID) {

        // Verify staff authorization.
        authUtils.validateStaffAccess(requestorID);
        
        // Verify copy exists and is available.
        Copy copy = copyDAO.findById(copyID);
        if (copy == null || copy.getStatus() != Copy.CopyStatus.AVAILABLE) {
            throw new IllegalArgumentException("Copy is not available for hold processing.");
        }
        
        // Find the next hold in queue.
        Hold nextHold = holdDAO.findNextHoldForTitle(titleID);
        if (nextHold == null) {
            throw new IllegalArgumentException("No holds in queue for this title.");
        }
        
        // Mark copy as reserved for this hold.
        copy.markReserved();
        copyDAO.update(copy);
        
        // Update hold status to ready for pickup.
        nextHold.setCopyID(copyID);
        nextHold.markReady(LocalDateTime.now()); 
        holdDAO.update(nextHold);
        
        return nextHold;
    }
    
    // 4. Complete hold pickup (STAFF only).
    public boolean completeHoldPickup(int holdID, int requestorID) {
        // Verify staff authorization.
        authUtils.validateStaffAccess(requestorID);
        
        Hold hold = holdDAO.findById(holdID);
        if (hold == null) {
            throw new IllegalArgumentException("Hold not found with ID: " + holdID);
        }
        
        if (hold.getStatus() != Hold.HoldStatus.READY) {
            throw new IllegalArgumentException("Hold is not ready for pickup.");
        }
        
        // Mark copy as checked out (this should integrate with LoanService)
        Copy copy = copyDAO.findById(hold.getCopyID());
        if (copy != null) {
            copy.markCheckedOut();
            copyDAO.update(copy);
        }
        
        // Mark hold as picked up.
        hold.markPickedUp();
        holdDAO.update(hold);

        // Reorder the queue for this title.
        reorderHoldQueue(hold.getTitleID());
        
        return true;
    }
    
    // 5. Handle expired holds (STAFF only - typically called by scheduled job).
    public List<Hold> processExpiredHolds(int requestorID) {
        
        // Verify staff role.
        authUtils.validateStaffAccess(requestorID);
        
        // Get all expired holds.
        List<Hold> expiredHolds = holdDAO.findExpiredHolds();
        
        // Iterate over list of expired holds and change the COPY status to available.
        for (Hold expiredHold : expiredHolds) {
            // Mark copy as available again.
            if (expiredHold.getCopyID() != null) {
                Copy copy = copyDAO.findById(expiredHold.getCopyID());
                if (copy != null && copy.getStatus() == Copy.CopyStatus.RESERVED) {
                    copy.markAvailable();
                    copyDAO.update(copy);
                }
            }
            
            // Mark hold as expired.
            expiredHold.markExpired();
            holdDAO.update(expiredHold);
            
            // Try to process next hold in queue.
            try {
                List<Copy> availableCopies = copyDAO.findAvailableByTitle(expiredHold.getTitleID());
                if (!availableCopies.isEmpty()) {
                    processNextHold(expiredHold.getTitleID(), availableCopies.get(0).getCopyID(), requestorID);
                }
            } catch (Exception e) {
                // Log error but continue processing other expired holds.
                System.err.println("Error processing next hold for title " + expiredHold.getTitleID() + ": " + e.getMessage());
            }
        }
        
        return expiredHolds;
    }
    
    // 6. Get user's holds (MEMBERS can see their own, STAFF can see any user's).
    public List<Hold> getUserHolds(int userID, int requestorID) {

        // Verify user status and also get their details.
        User requestor = authUtils.getRequestor(requestorID);
        
        // Check permissions.
        if (!requestor.isStaff() && userID != requestorID) {
            throw new AuthorizationFailedException("You can only view your own holds.");
        }
        
        return holdDAO.findByUser(userID);
    }
    
    // 7. Get holds for a specific title (STAFF only).
    public List<Hold> getHoldsForTitle(int titleID, int requestorID) {
        authUtils.validateStaffAccess(requestorID);
        
        return holdDAO.findByTitle(titleID);
    }
    
    // 8. Get hold queue position (MEMBERS & STAFF).
    public int getHoldPosition(int holdID, int requestorID) {
        User requestor = authUtils.getRequestor(requestorID);
        
        Hold hold = holdDAO.findById(holdID);
        if (hold == null) {
            throw new IllegalArgumentException("Hold not found with ID: " + holdID);
        }
        
        // Check permissions.
        if (!requestor.isStaff() && hold.getUserID() != requestorID) {
            throw new AuthorizationFailedException("You can only check your own hold positions.");
        }
        
        return hold.getPosition();
    }
    
    // 9. Check if user can place hold on title.
    public boolean canPlaceHold(int titleID, int userID, int requestorID) {
        User requestor = authUtils.getRequestor(requestorID);
        
        // Check permissions.
        if (!requestor.isStaff() && userID != requestorID) {
            throw new AuthorizationFailedException("You can only check your own hold eligibility.");
        }
        
        // Check if title exists and is visible.
        Title title = titleDAO.findById(titleID);
        if (title == null || !title.isVisible()) {
            return false;
        }
        
        // Check if user already has hold.
        if (holdDAO.findActiveHoldByUserAndTitle(userID, titleID) != null) {
            return false;
        }
        
        // Check if copies are available (shouldn't place hold if available).
        List<Copy> availableCopies = copyDAO.findAvailableByTitle(titleID);
        return availableCopies.isEmpty();
    }
}
