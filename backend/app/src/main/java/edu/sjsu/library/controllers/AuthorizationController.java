package edu.sjsu.library.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import edu.sjsu.library.services.UserService;
import edu.sjsu.library.models.User;

@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {

    private final UserService userService;
    private static final String SESSION_USER_ID = "USER_ID";

    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest body, HttpServletRequest req) {
        User user = userService.authenticate(body.email, body.password);
        HttpSession session = req.getSession(true);
        session.setAttribute(SESSION_USER_ID, user.getUserID());
        return ResponseEntity.ok(user);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.noContent().build();
    }

    // GET /api/auth/me
    @GetMapping("/me")
    public ResponseEntity<User> me(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return ResponseEntity.status(401).build();
        Integer id = (Integer) session.getAttribute(SESSION_USER_ID);
        if (id == null) return ResponseEntity.status(401).build();
        User u = userService.findById(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(u);
    }

    // DTO
    public static class LoginRequest {
        public String email;
        public String password;
    }
}
