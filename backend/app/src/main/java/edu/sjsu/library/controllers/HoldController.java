package edu.sjsu.library.controllers;

import edu.sjsu.library.dao.HoldDAO;
import edu.sjsu.library.models.Hold;
import edu.sjsu.library.models.Hold.HoldStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HoldController {

    private final HoldDAO holdDAO;

    public HoldController(HoldDAO holdDAO) {
        this.holdDAO = holdDAO;
    }

    // LIST HOLDS WITH OPTIONAL FILTERS
    @GetMapping("/holds")
    public String listHolds(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer titleId,
            @RequestParam(required = false) String status,
            Model model) {

        List<Hold> holds = holdDAO.findAll();

        // Filter by user
        if (userId != null) {
            holds = holds.stream()
                    .filter(h -> h.getUserID() == userId)
                    .collect(Collectors.toList());
        }

        // Filter by title
        if (titleId != null) {
            holds = holds.stream()
                    .filter(h -> h.getTitleID() == titleId)
                    .collect(Collectors.toList());
        }

        // Filter by status
        if (status != null && !status.isEmpty()) {
            String st = status.toUpperCase();
            holds = holds.stream()
                    .filter(h -> h.getStatus().name().equals(st))
                    .collect(Collectors.toList());
        }

        model.addAttribute("holds", holds);
        model.addAttribute("statuses", HoldStatus.values());

        return "holds/holds-list";
    }

    // VIEW ONE HOLD
    @GetMapping("/holds/{id}")
    public String viewHold(@PathVariable int id, Model model) {
        Hold h = holdDAO.findById(id);

        if (h == null) {
            model.addAttribute("error", "Hold not found with ID: " + id);
            return "error";
        }

        model.addAttribute("hold", h);
        return "holds/view-hold";
    }

    // SHOW ADD FORM
    @GetMapping("/holds/add")
    public String showAddForm(Model model) {
        model.addAttribute("statuses", HoldStatus.values());
        return "holds/add-hold";
    }

     // ADD HOLD
    @PostMapping("/holds/add")
    public String addHold(
            @RequestParam int userId,
            @RequestParam int titleId,
            @RequestParam int copyId,
            @RequestParam String status,
            Model model) {
        try {
            HoldStatus holdStatus = HoldStatus.valueOf(status.toUpperCase());

            int nextPos = holdDAO.getNextPosition(titleId);

            Hold newHold = new Hold(
                    0,
                    userId,
                    titleId,
                    copyId,
                    holdStatus,
                    LocalDateTime.now(),
                    null,
                    null,
                    nextPos
            );

            int id = holdDAO.insert(newHold);
            model.addAttribute("message", "Hold created successfully!");
            model.addAttribute("holdId", id);

            return "holds/add-hold-success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("statuses", HoldStatus.values());
            return "holds/add-hold";
        }
    }

    // SHOW EDIT FORM
    @GetMapping("/holds/{id}/edit")
    public String editHoldForm(@PathVariable int id, Model model) {
        Hold h = holdDAO.findById(id);
        if (h == null) {
            model.addAttribute("error", "Hold not found with ID: " + id);
            return "error";
        }

        model.addAttribute("hold", h);
        model.addAttribute("statuses", HoldStatus.values());
        return "holds/edit-hold";
    }

    // UPDATE HOLD
    @PostMapping("/holds/{id}/edit")
    public String updateHold(
            @PathVariable int id,
            @RequestParam int userId,
            @RequestParam int titleId,
            @RequestParam int copyId,
            @RequestParam String status,
            @RequestParam int position,
            Model model) {

        try {
            Hold existing = holdDAO.findById(id);
            if (existing == null) {
                model.addAttribute("error", "Hold not found with ID: " + id);
                return "error";
            }

            HoldStatus holdStatus = HoldStatus.valueOf(status.toUpperCase());

            // Preserve placedAt, readyAt, pickupExpire unless changed manually later
            existing.setUserID(userId);
            existing.setTitleID(titleId);
            existing.setCopyID(copyId);
            existing.setStatus(holdStatus);
            existing.setPosition(position);

            holdDAO.update(existing);

            model.addAttribute("message", "Hold updated successfully!");
            model.addAttribute("hold", existing);
            return "holds/edit-hold-success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    // DELETE HOLD
    @PostMapping("/holds/{id}/delete")
    public String deleteHold(@PathVariable int id, Model model) {
        try {
            int rows = holdDAO.delete(id);
            if (rows == 0) {
                model.addAttribute("error", "Hold not found or already deleted.");
                return "error";
            }
            return "redirect:/holds";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
