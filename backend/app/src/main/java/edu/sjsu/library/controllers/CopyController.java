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

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Copy>> getAllCopies(HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        List<Copy> copies = copyService.getAllCopies(requestorID);
        return ResponseEntity.ok(copies);
    }

    @GetMapping("/{copyID}")
    @ResponseBody
    public ResponseEntity<Copy> getCopyById(
            @PathVariable int copyID,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        Copy copy = copyService.getCopyById(copyID, requestorID);
        return copy != null ? ResponseEntity.ok(copy) : ResponseEntity.notFound().build();
    }

    @GetMapping("/title/{titleID}")
    @ResponseBody
    public ResponseEntity<List<Copy>> getCopiesForTitle(
            @PathVariable int titleID,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        List<Copy> copies = copyService.getCopiesForTitle(titleID, requestorID);
        return ResponseEntity.ok(copies);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Copy> addCopy(
            @RequestBody Copy newCopy,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        Copy created = copyService.addCopy(newCopy, requestorID);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{copyID}")
    @ResponseBody
    public ResponseEntity<Copy> updateCopy(
            @PathVariable int copyID,
            @RequestBody Copy updatedCopy,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        updatedCopy.setCopyID(copyID);
        Copy copy = copyService.updateCopy(updatedCopy, requestorID);
        return ResponseEntity.ok(copy);
    }

    @DeleteMapping("/{copyID}")
    @ResponseBody
    public ResponseEntity<Void> deleteCopy(
            @PathVariable int copyID,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        boolean success = copyService.deleteCopy(copyID, requestorID);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{copyID}/available")
    @ResponseBody
    public ResponseEntity<Boolean> isCopyAvailable(
            @PathVariable int copyID,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        boolean available = copyService.isCopyAvailable(copyID, requestorID);
        return ResponseEntity.ok(available);
    }

    @PostMapping("/{copyID}/status")
    @ResponseBody
    public ResponseEntity<Copy> changeCopyStatus(
            @PathVariable int copyID,
            @RequestParam Copy.CopyStatus status,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        Copy updated = copyService.changeCopyStatus(copyID, status, requestorID);
        return ResponseEntity.ok(updated);
    }
    @PostMapping("/{copyID}/visibility")
    @ResponseBody
    public ResponseEntity<Copy> setCopyVisibility(
            @PathVariable int copyID,
            @RequestParam boolean visible,
            HttpServletRequest request) {
        int requestorID = getRequestorId(request);
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
