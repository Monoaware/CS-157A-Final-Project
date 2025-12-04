package edu.sjsu.library.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import edu.sjsu.library.services.FineService;
import edu.sjsu.library.models.Fine;
import edu.sjsu.library.utils.AuthorizationUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/fines")
public class FineRestController {
    
    private final FineService fineService;
    private final AuthorizationUtils authUtils;

    public FineRestController(FineService fineService, AuthorizationUtils authUtils) {
        this.fineService = fineService;
        this.authUtils = authUtils;
    }

    // GET /api/fines - Get all fines (STAFF only)
    @GetMapping
    public ResponseEntity<List<Fine>> getAllFines(HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        
        List<Fine> fines = fineService.getAllFines(requestorID);
        return ResponseEntity.ok(fines);
    }

    // GET /api/fines/{id} - Get single fine
    @GetMapping("/{id}")
    public ResponseEntity<Fine> getFineById(@PathVariable int id, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        
        Fine fine = fineService.getFineById(id, requestorID);
        if (fine == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(fine);
    }

    // GET /api/fines/user/{userId} - Get fines for specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Fine>> getFinesByUser(@PathVariable int userId, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        
        List<Fine> fines = fineService.getFinesByUser(userId, requestorID);
        return ResponseEntity.ok(fines);
    }

    // GET /api/fines/user/{userId}/outstanding - Get outstanding fines for user
    @GetMapping("/user/{userId}/outstanding")
    public ResponseEntity<Map<String, Object>> getUserOutstandingFines(
            @PathVariable int userId, 
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        
        List<Fine> outstandingFines = fineService.getOutstandingFines(requestorID, userId);
        BigDecimal totalAmount = fineService.getTotalOutstandingAmount(requestorID, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("fines", outstandingFines);
        response.put("totalAmount", totalAmount);
        response.put("count", outstandingFines.size());
        
        return ResponseEntity.ok(response);
    }

    // POST /api/fines - Create new fine (STAFF only)
    @PostMapping
    public ResponseEntity<Fine> createFine(@RequestBody CreateFineRequest body, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        
        try {
            Fine fine = fineService.createFine(
                body.userId,
                body.loanId,
                new BigDecimal(body.amount),
                body.reason,
                requestorID
            );
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST /api/fines/{id}/waive - Waive fine (STAFF only)
    @PostMapping("/{id}/waive")
    public ResponseEntity<Fine> waiveFine(@PathVariable int id, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        
        try {
            Fine fine = fineService.waiveFine(id, requestorID);
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/fines/{id}/pay - Pay fine (Member only, their own fine)
    @PostMapping("/{id}/pay")
    public ResponseEntity<Fine> payFine(@PathVariable int id, HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        
        try {
            Fine fine = fineService.payFine(id, requestorID);
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Helper: Extract requestor ID from session
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

    // DTO for creating fines
    public static class CreateFineRequest {
        public int userId;
        public int loanId;
        public String amount;
        public String reason;
    }
}
