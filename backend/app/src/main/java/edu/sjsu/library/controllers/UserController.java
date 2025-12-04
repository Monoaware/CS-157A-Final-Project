package edu.sjsu.library.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.sjsu.library.services.UserService;
import edu.sjsu.library.models.User;
import edu.sjsu.library.utils.AuthorizationUtils;

// Use REST API!
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthorizationUtils authUtils;

    public UserController(UserService userService, AuthorizationUtils authUtils) {
        this.userService = userService;
        this.authUtils = authUtils;
    }

    // 1. GET /api/users?email=&role=&status=
    // STAFF: View ALL accounts with optional filters (email, role, status).
    // MEMBERS: Not allowed (validateStaffAccess will throw).
    @GetMapping
    public ResponseEntity<List<User>> listUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // STAFF only

        List<User> users = userService.getAllUsers(requestorID);
        users = applyFilters(users, email, role, status);
        return ResponseEntity.ok(users);
    }

    // 2. GET /api/users/me
    // Convenience endpoint: Get current user's own account.
    // STAFF or MEMBERS can access their own data.
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        User u = userService.findById(requestorID);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(u);
    }

    // 3. GET /api/users/{id}
    // STAFF: Can view any user account.
    // MEMBERS: Can only view their own account (validateUserDataAccess enforces this).
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        
        // This validates: staff can access any, members can only access their own.
        authUtils.validateUserDataAccess(requestorID, id);

        User u = userService.findById(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(u);
    }

    // 4. GET /api/users/staff
    // Get all staff users (STAFF only).
    @GetMapping("/staff")
    public ResponseEntity<List<User>> getStaffUsers(HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        return ResponseEntity.ok(userService.getAllStaff(requestorID));
    }

    // 5. GET /api/users/members
    // Get all member users (STAFF only).
    @GetMapping("/members")
    public ResponseEntity<List<User>> getMemberUsers(HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        return ResponseEntity.ok(userService.getAllMembers(requestorID));
    }

    // 6. POST /api/users
    // Create new user account (STAFF only or public registration - adjust as needed).
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest body, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // STAFF only for now

        User created = userService.register(
            body.fname, 
            body.lname, 
            body.email, 
            body.password, 
            body.role,
            requestorID
        );
        return ResponseEntity.status(201).body(created);
    }

    // 7. PUT /api/users/{id}
    // Update user account (STAFF can update any, MEMBERS can update their own).
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable int id, 
            @RequestBody UpdateUserRequest body, 
            HttpServletRequest request) {
        
        int requestorID = getRequestorId(request);
        authUtils.validateUserDataAccess(requestorID, id);

        User updated = userService.updateUser(id, body.fname, body.lname, body.email, body.status, requestorID);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // 8. PATCH /api/users/{id}/status
    // Change a member's status (STAFF only).
    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable int id,
            @RequestBody UpdateStatusRequest body,
            HttpServletRequest request) {
        
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // STAFF only

        User updated = userService.updateUserStatus(id, body.status, requestorID);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // 9. POST /api/users/{id}/activate
    // Activate user account (STAFF only).
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable int id, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        
        boolean success = userService.activateUser(id, requestorID);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 10. POST /api/users/{id}/deactivate
    // Deactivate user account (STAFF only).
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable int id, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        
        boolean success = userService.deactivateUser(id, requestorID);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 11. POST /api/users/{id}/restrict
    // Restrict user account (STAFF only).
    @PostMapping("/{id}/restrict")
    public ResponseEntity<Void> restrictUser(@PathVariable int id, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        
        boolean success = userService.restrictUser(id, requestorID);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Helper: Apply filters to user list (email, role, status).
    private List<User> applyFilters(List<User> users, String email, String role, String status) {
        // Filter by email (exact or partial match).
        if (email != null && !email.isEmpty()) {
            String emailLower = email.toLowerCase();
            users = users.stream()
                    .filter(u -> Optional.ofNullable(u.getEmail())
                            .orElse("")
                            .toLowerCase()
                            .contains(emailLower))
                    .collect(Collectors.toList());
        }

        // Filter by role.
        if (role != null && !role.isEmpty()) {
            String roleLower = role.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getRole() != null && u.getRole().name().toLowerCase().equals(roleLower))
                    .collect(Collectors.toList());
        }

        // Filter by status.
        if (status != null && !status.isEmpty()) {
            String statusLower = status.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getStatus() != null && u.getStatus().name().toLowerCase().equals(statusLower))
                    .collect(Collectors.toList());
        }

        return users;
    }

    // Extract requestor ID from session.
    private int getRequestorId(HttpServletRequest req) {
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        if (session == null) {
            throw new RuntimeException("Unauthenticated - no session.");
        }
        Integer id = (Integer) session.getAttribute("USER_ID");
        if (id == null) {
            throw new RuntimeException("Unauthenticated - USER_ID is missing from session.");
        }
        return id;
    }

    // DTOs for request bodies.
    public static class CreateUserRequest {
        public String fname;
        public String lname;
        public String email;
        public String password;
        public User.UserRole role;
    }

    public static class UpdateUserRequest {
        public String fname;
        public String lname;
        public String email;
        public User.UserStatus status;
    }

    public static class UpdateStatusRequest {
        public User.UserStatus status;
    }
}
