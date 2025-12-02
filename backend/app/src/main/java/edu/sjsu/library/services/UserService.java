/*
    UserService.java is specifically meant for user-only operations and does not handle any of the book-related business logic.
*/
package edu.sjsu.library.services;
import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.User;
import edu.sjsu.library.exceptions.UserAlreadyExistsException;
import edu.sjsu.library.exceptions.AuthenticationFailedException;
import edu.sjsu.library.exceptions.UserStatusChangeNotAllowedException;
import org.mindrot.jbcrypt.BCrypt;
import edu.sjsu.library.utils.AuthorizationUtils;
import java.util.List;
import java.util.stream.Collectors; 

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // Tell Springboot this is a service component so it can manage it for us.
public class UserService {
    private final UserDAO userDao;
    private final int bcryptWorkFactor = 12;
    private final AuthorizationUtils authUtils;

    // Constructor.
    public UserService(UserDAO userDao, AuthorizationUtils authUtils) {
        this.userDao = userDao;
        this.authUtils = authUtils;
    }

    // 1. Registration.
    @Transactional
    private User register(String fname, String lname, String email, String rawPassword, User.UserRole role) {
        // Public registration (no requestor) delegates to the main implementation.
        return register(fname, lname, email, rawPassword, role, null);
    }

    @Transactional
    public User register(String fname, String lname, String email, String rawPassword, User.UserRole role, Integer requestorID) {
        // If creating a staff account, ensure that the caller is staff.
        if (role == User.UserRole.STAFF) {
            if (requestorID == null) {
                throw new IllegalArgumentException("Only staff may create staff accounts.");
            }
            // validateStaffAccess will throw AuthenticationFailedException or AuthorizationFailedException if invalid.
            authUtils.validateStaffAccess(requestorID);
        }

        // Emails must be unique!
        if (userDao.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("Registration failed: email is already registered.");
        }
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(bcryptWorkFactor));
        User user = new User(fname, lname, email, hash, role);
        userDao.insert(user);
        return user;
    }

    // 2. Authentication.
    public User authenticate(String email, String rawPassword) {
        User user = userDao.findByEmail(email);
        if (user == null || !BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
            throw new AuthenticationFailedException("Authentication failed: invalid credentials.");
        }
        if (!user.isActive()) {
            throw new AuthenticationFailedException("Authentication failed: please see library staff to reactivate your account.");
        }
        return user;
    }

    // 3. Password change (NOT RESET). 
    @Transactional
    public void changePassword(int userID, String oldPassword, String newPassword) {
        User user = userDao.findById(userID);
        if (user == null) {
            throw new IllegalArgumentException("Password change failed: the user could not be located.");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            throw new AuthenticationFailedException("Password change failed: the old password did not match any stored passwords.");
        }
        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(bcryptWorkFactor));
        user.setPasswordHash(newHash);
        userDao.update(user);
    }

    // 4. Profile update.
    public void updateProfile(User updatedUser) {
        userDao.update(updatedUser);
    }

    // 5. Status change.
    @Transactional
    public void setStatus(int userID, User.UserStatus newStatus) {
        User user = userDao.findById(userID);
        if (user == null) throw new IllegalArgumentException("Status change failed: user not found.");

        User.UserStatus currentStatus = user.getStatus();
        
        // If status is already the desired status, skip the change
        if (currentStatus == newStatus) {
            return; // No change needed
        }
        
        if (currentStatus == User.UserStatus.RESTRICTED && newStatus == User.UserStatus.ACTIVE) {
            throw new UserStatusChangeNotAllowedException("Status change failed: please see library staff to lift restriction.");
        }

        switch (newStatus) {
            case ACTIVE:
                user.activate();
                break;
            case INACTIVE:
                user.deactivate();
                break;
            case RESTRICTED:
                user.restrict();
                break;
            default:
                throw new IllegalArgumentException("Unknown status: " + newStatus);
        }
        userDao.update(user);
    }
    
    // 6. User lookups.
    public User findByEmail(String email) { return userDao.findByEmail(email); }
    public User findById(int userID) { return userDao.findById(userID); }

    // 7. View all users (STAFF-only function).
    public List<User> getAllUsers(int requestorID) {

        // Verify staff authorization.
        authUtils.validateStaffAccess(requestorID);
    
        // Return all users.
        return userDao.findAll();
    }

    // 8. View all STAFF (STAFF-only function).
    public List<User> getAllStaff(int requestorID) {

        // Verify staff authorization.
        authUtils.validateStaffAccess(requestorID);
    
        // Get all users and filter for staff members.
        List<User> allUsers = userDao.findAll();
        return allUsers.stream()
                .filter(User::isStaff)
                .collect(Collectors.toList());
    }

    // 9. View all MEMBERS (STAFF-only function).
    public List<User> getAllMembers(int requestorID) {

        // Verify staff authorization.
        authUtils.validateStaffAccess(requestorID);
    
        // Get all users and filter for members (non-staff).
        List<User> allUsers = userDao.findAll();
        return allUsers.stream()
                .filter(user -> !user.isStaff())
                .collect(Collectors.toList());
    }

    // 10. Update user information (STAFF can update any, MEMBERS can update their own).
    @Transactional
    public User updateUser(int userID, String fname, String lname, String email, User.UserStatus status, int requestorID) {
        // Verify authorization - staff can update any, members can only update their own
        authUtils.validateUserDataAccess(requestorID, userID);

        User user = userDao.findById(userID);
        if (user == null) {
            return null;
        }

        // Update fields
        if (fname != null && !fname.isEmpty()) {
            user.setFname(fname);
        }
        if (lname != null && !lname.isEmpty()) {
            user.setLname(lname);
        }
        if (email != null && !email.isEmpty()) {
            // Check if new email is already taken by another user
            User existingUser = userDao.findByEmail(email);
            if (existingUser != null && existingUser.getUserID() != userID) {
                throw new UserAlreadyExistsException("Email is already in use by another user.");
            }
            user.setEmail(email);
        }
        
        // Only staff can change status through this method
        User requestor = authUtils.getRequestor(requestorID);
        if (status != null && requestor.isStaff()) {
            setStatus(userID, status);
        }

        userDao.update(user);
        return user;
    }

    // 11. Update user status (STAFF only).
    @Transactional
    public User updateUserStatus(int userID, User.UserStatus newStatus, int requestorID) {
        // Verify staff authorization
        authUtils.validateStaffAccess(requestorID);

        User user = userDao.findById(userID);
        if (user == null) {
            return null;
        }

        setStatus(userID, newStatus);
        return userDao.findById(userID);
    }

    // 12. Activate user account (STAFF only).
    @Transactional
    public boolean activateUser(int userID, int requestorID) {
        // Verify staff authorization
        authUtils.validateStaffAccess(requestorID);

        User user = userDao.findById(userID);
        if (user == null) {
            return false;
        }

        user.activate();
        userDao.update(user);
        return true;
    }

    // 13. Deactivate user account (STAFF only).
    @Transactional
    public boolean deactivateUser(int userID, int requestorID) {
        // Verify staff authorization
        authUtils.validateStaffAccess(requestorID);

        User user = userDao.findById(userID);
        if (user == null) {
            return false;
        }

        user.deactivate();
        userDao.update(user);
        return true;
    }
}
