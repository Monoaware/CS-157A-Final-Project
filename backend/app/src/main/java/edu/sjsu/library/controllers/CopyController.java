package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import edu.sjsu.library.models.Copy;
import edu.sjsu.library.services.CopyService;
import edu.sjsu.library.utils.AuthorizationUtils;

@Controller
@RequestMapping("/api/copies")
public class CopyController {

    private final CopyService copyService;
    private final AuthorizationUtils authUtils;

    public CopyController(CopyService copyService, AuthorizationUtils authUtils) {
        this.copyService = copyService;
        this.authUtils = authUtils;
    }

    // GET /api/copies
    // Get all copies (STAFF ONLY).
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Copy>> getAllCopies(HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only
        List<Copy> copies = copyService.getAllCopies(requestorID);
        return ResponseEntity.ok(copies);
    }

    // GET /api/copies/{copyID}
    // Get copy by ID (STAFF ONLY).
    @GetMapping("/{copyID}")
    @ResponseBody
    public ResponseEntity<Copy> getCopyById(
            @PathVariable int copyID,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.
        Copy copy = copyService.getCopyById(copyID, requestorID);
        return copy != null ? ResponseEntity.ok(copy) : ResponseEntity.notFound().build();
    }

    // GET /api/copies/title/{titleID}
    // Get copies for a title (MEMBERS can access, service filters visibility).
    @GetMapping("/title/{titleID}")
    @ResponseBody
    public ResponseEntity<List<Copy>> getCopiesForTitle(
            @PathVariable int titleID,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        List<Copy> copies = copyService.getCopiesForTitle(titleID, requestorID);
        return ResponseEntity.ok(copies);
    }

    // POST /api/copies
    // Add new copy (STAFF ONLY).
    @PostMapping
    @ResponseBody
    public ResponseEntity<Copy> addCopy(
            @RequestBody Copy newCopy,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.
        Copy created = copyService.addCopy(newCopy, requestorID);
        return ResponseEntity.status(201).body(created);
    }

    // PUT /api/copies/{copyID}
    // Update copy (STAFF ONLY).
    @PutMapping("/{copyID}")
    @ResponseBody
    public ResponseEntity<Copy> updateCopy(
            @PathVariable int copyID,
            @RequestBody Copy updatedCopy,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.
        updatedCopy.setCopyID(copyID);
        Copy copy = copyService.updateCopy(updatedCopy, requestorID);
        return ResponseEntity.ok(copy);
    }

    // DELETE /api/copies/{copyID}
    // Delete copy (STAFF ONLY).
    @DeleteMapping("/{copyID}")
    @ResponseBody
    public ResponseEntity<Void> deleteCopy(
            @PathVariable int copyID,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.
        boolean success = copyService.deleteCopy(copyID, requestorID);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // GET /api/copies/{copyID}/available
    // Check if copy is available (MEMBERS can access, service enforces visibility).
    @GetMapping("/{copyID}/available")
    @ResponseBody
    public ResponseEntity<Boolean> isCopyAvailable(
            @PathVariable int copyID,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        boolean available = copyService.isCopyAvailable(copyID, requestorID);
        return ResponseEntity.ok(available);
    }

    // POST /api/copies/{copyID}/status
    // Change copy status (STAFF ONLY).
    @PostMapping("/{copyID}/status")
    @ResponseBody
    public ResponseEntity<Copy> changeCopyStatus(
            @PathVariable int copyID,
            @RequestParam Copy.CopyStatus status,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.
        Copy updated = copyService.changeCopyStatus(copyID, status, requestorID);
        return ResponseEntity.ok(updated);
    }
    // POST /api/copies/{copyID}/visibility
    // Set copy visibility (STAFF ONLY).
    @PostMapping("/{copyID}/visibility")
    @ResponseBody
    public ResponseEntity<Copy> setCopyVisibility(
            @PathVariable int copyID,
            @RequestParam boolean visible,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.
        Copy updated = copyService.setCopyVisibility(copyID, visible, requestorID);
        return ResponseEntity.ok(updated);
    }

    private int getRequestorId(HttpServletRequest req) {
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        if (session == null)
            throw new RuntimeException("Unauthenticated - no session.");

        Integer id = (Integer) session.getAttribute("USER_ID");
        if (id == null)
            throw new RuntimeException("Unauthenticated - USER_ID missing from session.");

        return id;
    }
}
