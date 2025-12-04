package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import edu.sjsu.library.models.Hold;
import edu.sjsu.library.services.HoldService;
import edu.sjsu.library.utils.AuthorizationUtils;

@Controller
@RequestMapping("/api/holds")
public class HoldController {

    private final HoldService holdService;
    private final AuthorizationUtils authUtils;

    public HoldController(HoldService holdService, AuthorizationUtils authUtils) {
        this.holdService = holdService;
        this.authUtils = authUtils;
    }

    // GET /api/holds
    // Get all holds (STAFF ONLY).
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Hold>> getAllHolds(HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);
        
        List<Hold> holds = holdService.getAllHolds(requestorID);
        return ResponseEntity.ok(holds);
    }

    // POST /api/holds/{titleID}
    // Place hold on a title (MEMBERS ONLY - staff cannot place holds).
    @PostMapping("/{titleID}")
    @ResponseBody
    public ResponseEntity<Hold> placeHold(
            @PathVariable int titleID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);

        Hold created = holdService.placeHold(titleID, requestorID);
        return ResponseEntity.status(201).body(created);
    }

    // DELETE /api/holds/{holdID}
    // Cancel hold (MEMBERS cancel own, STAFF can cancel any).
    @DeleteMapping("/{holdID}")
    @ResponseBody
    public ResponseEntity<Void> cancelHold(
            @PathVariable int holdID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);

        boolean success = holdService.cancelHold(holdID, requestorID);
        return success ? ResponseEntity.noContent().build()
                       : ResponseEntity.notFound().build();
    }

    // POST /api/holds/process/{titleID}/{copyID}.
    // Process next hold when copy available (STAFF ONLY).
    @PostMapping("/process/{titleID}/{copyID}")
    @ResponseBody
    public ResponseEntity<Hold> processNextHold(
            @PathVariable int titleID,
            @PathVariable int copyID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.

        Hold updated = holdService.processNextHold(titleID, copyID, requestorID);
        return ResponseEntity.ok(updated);
    }


    // POST /api/holds/pickup/{holdID}
    // Complete hold pickup (STAFF ONLY).
    @PostMapping("/pickup/{holdID}")
    @ResponseBody
    public ResponseEntity<Void> completeHoldPickup(
            @PathVariable int holdID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.

        boolean success = holdService.completeHoldPickup(holdID, requestorID);
        return success ? ResponseEntity.noContent().build()
                       : ResponseEntity.notFound().build();
    }

    // POST /api/holds/expired.
    // Process expired holds (STAFF ONLY - typically scheduled job).
    @PostMapping("/expired")
    @ResponseBody
    public ResponseEntity<List<Hold>> processExpiredHolds(HttpServletRequest request) {
        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.
        List<Hold> expired = holdService.processExpiredHolds(requestorID);
        return ResponseEntity.ok(expired);
    }

    // GET /api/holds/user/{userID}
    // Get user's holds (MEMBERS see own, STAFF can see any).
    @GetMapping("/user/{userID}")
    @ResponseBody
    public ResponseEntity<List<Hold>> getUserHolds(
            @PathVariable int userID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);

        List<Hold> holds = holdService.getUserHolds(userID, requestorID);
        return ResponseEntity.ok(holds);
    }

    // GET /api/holds/title/{titleID}
    // Get holds for a title (STAFF ONLY).
    @GetMapping("/title/{titleID}")
    @ResponseBody
    public ResponseEntity<List<Hold>> getHoldsForTitle(
            @PathVariable int titleID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID); // Staff only.

        List<Hold> holds = holdService.getHoldsForTitle(titleID, requestorID);
        return ResponseEntity.ok(holds);
    }

    // GET /api/holds/{holdID}/position
    // Get hold queue position (MEMBERS see own, STAFF can see any).
    @GetMapping("/{holdID}/position")
    @ResponseBody
    public ResponseEntity<Integer> getHoldPosition(
            @PathVariable int holdID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);

        int pos = holdService.getHoldPosition(holdID, requestorID);
        return ResponseEntity.ok(pos);
    }

    // GET /api/holds/canPlace
    // Check if user can place hold (MEMBERS check own, STAFF can check any).
    @GetMapping("/canPlace")
    @ResponseBody
    public ResponseEntity<Boolean> canPlaceHold(
            @RequestParam int titleID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);

        boolean can = holdService.canPlaceHold(titleID, requestorID, requestorID);
        return ResponseEntity.ok(can);
    }

    // POST /api/holds/{holdID}/ready
    // Mark hold as ready for pickup (STAFF ONLY).
    @PostMapping("/{holdID}/ready")
    @ResponseBody
    public ResponseEntity<Hold> markHoldReady(
            @PathVariable int holdID,
            @RequestParam int copyID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);

        Hold updated = holdService.markHoldReady(holdID, copyID, requestorID);
        return ResponseEntity.ok(updated);
    }

    // POST /api/holds/{holdID}/expire
    // Mark hold as expired (STAFF ONLY).
    @PostMapping("/{holdID}/expire")
    @ResponseBody
    public ResponseEntity<Hold> markHoldExpired(
            @PathVariable int holdID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        authUtils.validateStaffAccess(requestorID);

        Hold updated = holdService.markHoldExpired(holdID, requestorID);
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
