package edu.sjsu.library.services;
import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.User;
import edu.sjsu.library.exceptions.UserAlreadyExistsException;
import edu.sjsu.library.exceptions.AuthenticationFailedException;
import edu.sjsu.library.exceptions.UserStatusChangeNotAllowedException;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDao;
    private final int bcryptWorkFactor = 12;

    // Constructor.
    public UserService(UserDAO userDao) {
        this.userDao = userDao;
    }

    // 1. Registration.
    public User register(String fname, String lname, String email, String rawPassword, User.UserRole role) {
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
    public void setStatus(int userID, User.UserStatus newStatus) {
        User user = userDao.findById(userID);
        if (user == null) throw new IllegalArgumentException("Status change failed: user not found.");

        User.UserStatus currentStatus = user.getStatus();
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
}
