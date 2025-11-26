/*
    Helper functions for authorization checks in project services.
*/
package edu.sjsu.library.utils;

import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.User;
import edu.sjsu.library.exceptions.AuthenticationFailedException;
import edu.sjsu.library.exceptions.AuthorizationFailedException;

import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtils {
    
    private final UserDAO userDAO;

    // Constructor:
    public AuthorizationUtils(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Validates that the user exists (basic authentication).
     * @param requestorID The ID of the user making the request
     * @throws AuthenticationFailedException if user not found
     */
    public void validateUserAccess(int requestorID) {
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }
    }

    /**
     * Gets the user making the request (for services that need user details).
     * @param requestorID The ID of the user making the request
     * @return The User object
     * @throws AuthenticationFailedException if user not found
     */
    public User getRequestor(int requestorID) {
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }
        return requestor;
    }

    /**
     * Validates that the user exists and has staff privileges.
     * @param requestorID The ID of the user making the request
     * @throws AuthenticationFailedException if user not found
     * @throws AuthorizationFailedException if user is not staff
     */
    public void validateStaffAccess(int requestorID) {
        User requestor = userDAO.findById(requestorID) {
            if (requestor == null) {
                throw new AuthenticationFailedException("User not found.");
            }
            if (!requestor.isStaff()) {
                throw new AuthorizationFailedExceptions("Staff access is required for this operation.");
            }
        }
    }

    /**
     * Validates that the user can access another user's data.
     * (staff can access anyone's data, members can only access their own)
     * @param requestorID The ID of the user making the request
     * @param targetUserID The ID of the user whose data is being accessed
     * @throws AuthenticationFailedException if requestor not found
     * @throws AuthorizationFailedException if access not allowed
     */
    public void validateUserDataAccess(int requestorID, int targetUserID) {
        User requestor = getRequestor(requestorID);
        
        // Staff can access anyone's data
        if (requestor.isStaff()) {
            return;
        }
        
        // Members can only access their own data
        if (requestor.getUserID() != targetUserID) {
            throw new AuthorizationFailedException("You can only access your own data.");
        }
    }
}