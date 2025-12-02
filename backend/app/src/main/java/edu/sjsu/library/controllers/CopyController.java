package edu.sjsu.library.controllers;

import edu.sjsu.library.dao.CopyDAO;
import edu.sjsu.library.models.Copy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CopyController {

    private final CopyDAO copyDAO;

    public CopyController(CopyDAO copyDAO) {
        this.copyDAO = copyDAO;
    }

    // -----------------------------------
    // LIST ALL COPIES + FILTERING
    // -----------------------------------
    @GetMapping("/copies")
    public String listCopies(
            @RequestParam(required = false) Integer titleId,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) Boolean visible,
            Model model) {

        List<Copy> copies;

        if (titleId != null) {
            copies = copyDAO.findByTitle(titleId);
        }
        else if (barcode != null && !barcode.isBlank()) {
            Copy c = copyDAO.findByBarcode(barcode);
            copies = (c == null) ? List.of() : List.of(c);
        }
        else if (visible != null) {
            // filter in-memory because DB stores boolean already
            copies = copyDAO.findAll().stream()
                    .filter(c -> c.isVisible() == visible)
                    .toList();
        }
        else {
            copies = copyDAO.findAll();
        }

        model.addAttribute("copies", copies);
        return "copies";
    }

    // -----------------------------------
    // VIEW A SINGLE COPY
    // -----------------------------------
    @GetMapping("/copies/{copyId}")
    public String viewCopy(@PathVariable int copyId, Model model) {
        Copy copy = copyDAO.findById(copyId);

        if (copy == null) {
            model.addAttribute("error", "Copy not found with ID: " + copyId);
            return "error";
        }

        model.addAttribute("copy", copy);
        return "copies/view-copy";
    }

    // -----------------------------------
    // SHOW ADD FORM
    // -----------------------------------
    @GetMapping("/copies/add")
    public String showAddForm(Model model) {
        return "copies/add-copy";
    }

    // -----------------------------------
    // HANDLE ADD
    // -----------------------------------
    @PostMapping("/copies/add")
    public String addCopy(
            @RequestParam int titleID,
            @RequestParam String barcode,
            @RequestParam String status,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "true") boolean isVisible,
            Model model) {

        try {
            Copy newCopy = new Copy(
                    0,
                    titleID,
                    barcode,
                    Copy.CopyStatus.valueOf(status.toUpperCase().trim()),
                    location,
                    isVisible
            );

            int newId = copyDAO.insert(newCopy);

            model.addAttribute("message", "Copy added successfully!");
            model.addAttribute("copyId", newId);
            return "copies/add-copy-success";

        } catch (Exception e) {
            model.addAttribute("error", "Error adding copy: " + e.getMessage());
            return "copies/add-copy";
        }
    }

    // -----------------------------------
    // SHOW EDIT FORM
    // -----------------------------------
    @GetMapping("/copies/{copyId}/edit")
    public String showEditForm(@PathVariable int copyId, Model model) {
        Copy copy = copyDAO.findById(copyId);

        if (copy == null) {
            model.addAttribute("error", "Copy not found with ID: " + copyId);
            return "error";
        }

        model.addAttribute("copy", copy);
        return "copies/edit-copy";
    }

    // -----------------------------------
    // HANDLE EDIT
    // -----------------------------------
    @PostMapping("/copies/{copyId}/edit")
    public String editCopy(
            @PathVariable int copyId,
            @RequestParam int titleID,
            @RequestParam String barcode,
            @RequestParam String status,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "true") boolean isVisible,
            Model model) {

        try {
            Copy existing = copyDAO.findById(copyId);

            if (existing == null) {
                model.addAttribute("error", "Copy not found with ID: " + copyId);
                return "error";
            }

            existing.setTitleID(titleID);
            existing.setBarcode(barcode);
            existing.setStatus(Copy.CopyStatus.valueOf(status.toUpperCase().trim()));
            existing.setLocation(location);
            existing.setVisible(isVisible);

            copyDAO.update(existing);

            model.addAttribute("message", "Copy updated successfully!");
            model.addAttribute("copy", existing);
            return "copies/edit-copy-success";

        } catch (Exception e) {
            model.addAttribute("error", "Error updating copy: " + e.getMessage());
            return "copies/edit-copy";
        }
    }

    // -----------------------------------
    // DELETE A COPY
    // -----------------------------------
    @PostMapping("/copies/{copyId}/delete")
    public String deleteCopy(@PathVariable int copyId, Model model) {
        try {
            int deleted = copyDAO.delete(copyId);

            if (deleted == 0) {
                model.addAttribute("error", "Unable to delete copy: ID not found.");
                return "error";
            }

            model.addAttribute("message", "Copy deleted successfully!");
            return "copies/delete-copy-success";

        } catch (Exception e) {
            model.addAttribute("error", "Error deleting copy: " + e.getMessage());
            return "error";
        }
    }
}
