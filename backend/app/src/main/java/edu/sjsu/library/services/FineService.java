/*
    FineService.java handles all major fine operations.
*/
package edu.sjsu.library.services;

// Adding dependencies...
import edu.sjsu.library.dao.FineDAO;
import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.Fine;
import edu.sjsu.library.models.User;
import edu.sjsu.library.exceptions.FinePaymentNotAllowedException;
import edu.sjsu.library.exceptions.FineWaivementNotAllowedException;
import edu.sjsu.library.exceptions.AuthorizationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import edu.sjsu.library.models.Fine.FineStatus;
import edu.sjsu.library.exceptions.AuthenticationFailedException;


import java.util.List;

@Service
@Transactional // Most of the services require multiple database operations.
public class FineService {
    private final FineDAO fineDAO;
    private final UserDAO userDAO;

    // Constructor:
    public FineService(FineDAO fineDAO, UserDAO userDAO) {
        this.fineDAO = fineDAO;
        this.userDAO = userDAO;
    }

    // 1. Get all fines for a user (don't support individual fine display).
    public List<Fine> getUserFines(int requestorID, int subjectUserID) {
        // Find user who is requesting the service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }

        // Validate that the subject user exists.
        User subjectUser = userDAO.findById(subjectUserID);
        if (subjectUser == null) {
            throw new IllegalArgumentException("Subject user not found with ID: " + subjectUserID);
        }

        // Authorization consideration: users can only view their own fines, but staff can view all members' fines.
        if (!requestor.isStaff() && requestorID != subjectUserID) {
            throw new AuthorizationFailedException("You can only view your own fines.");
        }

        return fineDAO.findByUser(subjectUserID);
    }

    // 2. Get all outstanding fines for a user.
    public List<Fine> getOutstandingFines(int requestorID, int subjectUserID) {
        // Find the user who is requesting the service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }

        // Validate that the subject user exists.
        User subjectUser = userDAO.findById(subjectUserID);
        if (subjectUser == null) {
            throw new IllegalArgumentException("Subject user not found with ID: " + subjectUserID);
        }

        // Authorization consideration: users can only view their own outstanding fines, but staff can view all members' outstanding fines.
        if (!requestor.isStaff() && requestorID != subjectUserID) {
            throw new AuthorizationFailedException("You can only view your own fines.");
        }

        return fineDAO.findByUserAndStatus(subjectUserID, FineStatus.UNPAID);
    }

    // 3. Pay a fine. This is intended only for MEMBERS. 
    // This also implies that fines CANNOT be paid partially (for now).
    public void payFine(int fineID, int requestorID) {
        // Get the fine and the user that it belongs to from the database.
        Fine fine = fineDAO.findById(fineID);
        if (fine == null) {
            throw new IllegalArgumentException("Fine not found with ID: " + fineID);
        }

        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }

        // Authorization & authentication concern: 
        // Only the user to whom the fine belongs to can pay it.
        if (fine.getUserID() != requestorID) {
            throw new AuthorizationFailedException("You can only pay your own fines.");
        }

        // Pay the fine (don't care about actually payment details for now).
        fine.payFine();

        // Persist the change to DB:
        fineDAO.update(fine);
    }

    // 4. Waive a fine. THIS IS AN ADMIN ONLY ACTION!
    public void waiveFine(int fineID, int requestorID) {
        // Check that the user calling this function is STAFF:
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }
        
        if  (!requestor.isStaff()) {
            throw new AuthorizationFailedException("Access denied: staff privileges required.");
        } 

        // Get the fine:
        Fine fine = fineDAO.findById(fineID);
        if (fine == null) {
            throw new IllegalArgumentException("Fine not found with ID: " + fineID);
        }

        // Waive the fine:
        fine.waiveFine();

        // Persist the change to DB:
        fineDAO.update(fine);
    }

    // 5. Create a new fine. THIS IS AN ADMIN ONLY ACTION!
    public Fine createFine(int subjectUserID, int loanID, BigDecimal amount, String reason, int requestorID) {
        User requestor = userDAO.findById(requestorID);
        // Authorization concern: only library STAFF can create new fines.
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        } 
        
        if (!requestor.isStaff()) {
            throw new AuthorizationFailedException("Access denied: staff privileges required.");
        }

        // Validate fine amount.
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Fine amount must be positive: " + amount);
        }

        // Validate that the subject user exists.
        User subjectUser = userDAO.findById(subjectUserID);
        if (subjectUser == null) {
            throw new IllegalArgumentException("Subject user not found with ID: " + subjectUserID);
        }

        // Call the Fine constructor.
        Fine fine = new Fine(subjectUserID, loanID, amount, reason);

        // Insert the fine into the database:
        fineDAO.insert(fine);

        // Return the created fine (whoever called this might want to use it).
        return fine;
    }

    // 6. Calculate total outstanding amount for user.
    public BigDecimal getTotalOutstandingAmount(int requestorID, int subjectUserID) {
        // Authorization consideration: check that the requestor is either STAFF or the same as the subject (handled by getOutstandingFines()).
        List<Fine> outstandingFines = getOutstandingFines(requestorID, subjectUserID);
        return outstandingFines.stream()
            .map(Fine::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // 7. Check if user has any outstanding fines.
    public boolean hasOutstandingFines(int requestorID, int subjectUserID) {
        // Authorization concern: STAFF should be able to view this for all users, MEMBERS can only view their own fines.
        // Handled by getOutstandingFines().
        return !getOutstandingFines(requestorID, subjectUserID).isEmpty();
    }
}
