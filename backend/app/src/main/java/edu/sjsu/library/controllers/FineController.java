package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import edu.sjsu.library.services.FineService;
import edu.sjsu.library.services.UserService;
import edu.sjsu.library.models.Fine;
import edu.sjsu.library.models.User;
import edu.sjsu.library.utils.AuthorizationUtils;

import java.math.BigDecimal;

import java.util.List;

@Controller
@RequestMapping("/fines")
public class FineController {
    
    private final FineService fineService;
    private final UserService userService;
    private final AuthorizationUtils authUtils;

    public FineController(FineService fineService, UserService userService, AuthorizationUtils authUtils) {
        this.fineService = fineService;
        this.userService = userService;
        this.authUtils = authUtils;
    }

    // GET /fines 
    // List fines (STAFF: all fines with filters, MEMBERS: own fines only)
    @GetMapping
    public String listFines(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            HttpServletRequest request,
            Model model) {
        
        int requestorID = getRequestorId(request);
        User requestor = authUtils.getRequestor(requestorID);
        
        List<Fine> fines;
        
        if (requestor.isStaff()) {
            // Staff can view all fines or filter by user.
            if (userId != null) {
                fines = fineService.getFinesByUser(userId, requestorID);
            } else {
                fines = fineService.getAllFines(requestorID);
            }
            
            // Apply status filter if provided.
            if (status != null && !status.isEmpty()) {
                Fine.FineStatus fineStatus = Fine.FineStatus.valueOf(status.toUpperCase());
                fines = fines.stream()
                        .filter(f -> f.getStatus() == fineStatus)
                        .collect(java.util.stream.Collectors.toList());
            }
            
            model.addAttribute("isStaff", true);
            model.addAttribute("users", userService.getAllMembers(requestorID)); // For filter dropdown
        } else {
            // Members can only view their own fines
            fines = fineService.getFinesByUser(requestorID, requestorID);
            model.addAttribute("isStaff", false);
        }
        
        model.addAttribute("fines", fines);
        model.addAttribute("currentUser", requestor);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("selectedStatus", status);
        
        return "fines"; // Resolves to src/main/resources/templates/fines.html
    }

    // GET /fines/{id} 
    // View single fine details.
    @GetMapping("/{id}")
    public String viewFine(@PathVariable int id, HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        User requestor = authUtils.getRequestor(requestorID);
        
        Fine fine = fineService.getFineById(id, requestorID);
        if (fine == null) {
            model.addAttribute("error", "Fine not found.");
            return "error";
        }
        
        // Check authorization - staff can view any, members only their own.
        if (!requestor.isStaff() && fine.getUserID() != requestorID) {
            model.addAttribute("error", "You can only view your own fines.");
            return "error";
        }
        
        model.addAttribute("fine", fine);
        model.addAttribute("isStaff", requestor.isStaff());
        model.addAttribute("currentUser", requestor);
        
        return "fine-detail"; // Resolves to src/main/resources/templates/fine-detail.html
    }

    // POST /fines/{id}/pay 
    // Member pays their fine.
    @PostMapping("/{id}/pay")
    public String payFine(@PathVariable int id, HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        
        try {
            fineService.payFine(id, requestorID);
            model.addAttribute("success", "Fine paid successfully!");
            return "redirect:/fines";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to pay fine: " + e.getMessage());
            return "redirect:/fines/" + id;
        }
    }

    // POST /fines/{id}/waive
    // Staff waives a fine.
    @PostMapping("/{id}/waive")
    public String waiveFine(@PathVariable int id, HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        
        try {
            authUtils.validateStaffAccess(requestorID); // Fast-fail for staff check.
            fineService.waiveFine(id, requestorID);
            model.addAttribute("success", "Fine waived successfully!");
            return "redirect:/fines";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to waive fine: " + e.getMessage());
            return "redirect:/fines/" + id;
        }
    }

    // GET /fines/user/{userId} 
    // Staff views fines for specific user.
    @GetMapping("/user/{userId}")
    public String viewUserFines(@PathVariable int userId, HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.
        
        User user = userService.findById(userId);
        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error";
        }
        
        List<Fine> fines = fineService.getFinesByUser(userId, requestorID);
        
        model.addAttribute("fines", fines);
        model.addAttribute("user", user);
        model.addAttribute("isStaff", true);
        
        return "user-fines"; // Resolves to src/main/resources/templates/user-fines.html
    }

    // GET /fines/create
    // Staff-only: displays form to create a fine for a user/book.
    @GetMapping("/create")
    public String showCreateFineForm(HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only

        model.addAttribute("users", userService.getAllMembers(requestorID));
        model.addAttribute("fine", new Fine(0, 0, BigDecimal.ZERO, ""));
        return "fine-create"; // form page
    }

    // POST /fines/create
    // Staff-only: creates a fine for a specific user and loan.
    @PostMapping("/create")
    public String createFine(
            @RequestParam int userId,
            @RequestParam int loanId,
            @RequestParam String amount,
            @RequestParam String reason,
            HttpServletRequest request,
            Model model) {

        int requestorID = getRequestorId(request);

        try {
            authUtils.validateStaffAccess(requestorID); // Ensure staff

            // Convert String → BigDecimal safely
            BigDecimal fineAmount = new BigDecimal(amount);

            fineService.createFine(userId, loanId, fineAmount, reason, requestorID);

            model.addAttribute("success", "Fine created successfully.");
            return "redirect:/fines";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create fine: " + e.getMessage());
            model.addAttribute("users", userService.getAllMembers(requestorID));
            return "fine-create";
        }
    }



    // Helper: Extract requestor ID from session.
    private int getRequestorId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            throw new RuntimeException("Unauthenticated - no session.");
        }
        Integer id = (Integer) session.getAttribute("USER_ID");
        if (id == null) {
            throw new RuntimeException("Unauthenticated - USER_ID is missing from session.");
        }
        return id;
    }
}
