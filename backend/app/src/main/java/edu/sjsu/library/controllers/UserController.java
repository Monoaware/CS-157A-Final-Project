package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.User;
import edu.sjsu.library.services.UserService;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class UserController {

    private final UserDAO userdao;
    private final UserService userService;

    public UserController(UserDAO userdao, UserService userService) {
        this.userdao = userdao;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            Model model){
        
        List<User> users = userdao.findAll();
        
        // Filter by search term (name or email)
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            users = users.stream()
                .filter(u -> u.getFname().toLowerCase().contains(searchLower) ||
                           u.getLname().toLowerCase().contains(searchLower) ||
                           u.getEmail().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
        }
        
        // Filter by role
        if (role != null && !role.isEmpty()) {
            users = users.stream()
                .filter(u -> u.getRole().name().equals(role))
                .collect(Collectors.toList());
        }
        
        // Filter by status
        if (status != null && !status.isEmpty()) {
            users = users.stream()
                .filter(u -> u.getStatus().name().equals(status))
                .collect(Collectors.toList());
        }
        
        model.addAttribute("users", users);
        
        // Calculate active users count
        long activeUsersCount = users.stream()
            .filter(User::isActive)
            .count();
        model.addAttribute("activeUsersCount", activeUsersCount);
        
        return "users";
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("roles", User.UserRole.values());
        return "users/add-user";
    }

    @PostMapping("/users/add")
    public String addUser(
            @RequestParam String fname,
            @RequestParam String lname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            Model model) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role);
            User newUser = userService.register(fname, lname, email, password, userRole);
            model.addAttribute("message", "User created successfully!");
            model.addAttribute("user", newUser);
            return "users/add-user-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", User.UserRole.values());
            return "users/add-user";
        }
    }
}