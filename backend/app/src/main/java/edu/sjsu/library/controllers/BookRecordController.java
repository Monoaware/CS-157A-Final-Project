package edu.sjsu.library.controllers;

import edu.sjsu.library.dao.BookRecordDAO;
import edu.sjsu.library.models.BookRecord;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BookRecordController {

    private final BookRecordDAO bookRecordDAO;

    public BookRecordController(BookRecordDAO bookRecordDAO) {
        this.bookRecordDAO = bookRecordDAO;
    }

    // LIST ALL / FILTERED LOANS
    @GetMapping("/records")
    public String listRecords(
            @RequestParam(required = false) Integer user,
            @RequestParam(required = false) Integer copy,
            @RequestParam(required = false) Integer title,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean overdue,
            Model model) {

        List<BookRecord> records;

        // Filtering priority
        if (user != null) {
            records = (active != null && active)
                    ? bookRecordDAO.findActiveByUser(user)
                    : bookRecordDAO.findByUser(user);
        }
        else if (copy != null) {
            records = bookRecordDAO.findByCopy(copy);
        }
        else if (title != null) {
            records = bookRecordDAO.findByTitle(title);
        }
        else if (overdue != null && overdue) {
            records = bookRecordDAO.findOverdue(LocalDateTime.now());
        }
        else {
            records = bookRecordDAO.findAll();
        }

        model.addAttribute("records", records);
        return "records";
    }

    // VIEW SINGLE RECORD
    @GetMapping("/records/{loanId}")
    public String viewRecord(@PathVariable int loanId, Model model) {
        try {
            BookRecord record = bookRecordDAO.findById(loanId);
            if (record == null) {
                model.addAttribute("error", "Book record not found with Loan ID: " + loanId);
                return "error";
            }

            model.addAttribute("record", record);
            return "records/view-record";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    // SHOW ADD FORM
    @GetMapping("/records/add")
    public String showAddForm(Model model) {
        return "records/add-record";
    }


    // HANDLE ADD
    @PostMapping("/records/add")
    public String addRecord(
            @RequestParam int copyID,
            @RequestParam int userID,
            @RequestParam String checkoutDate,
            @RequestParam String dueDate,
            Model model) {

        try {
            BookRecord newRec = new BookRecord(
                    0,
                    copyID,
                    userID,
                    LocalDateTime.parse(checkoutDate),
                    LocalDateTime.parse(dueDate),
                    null,
                    0 // renewCount
            );

            int newId = bookRecordDAO.insert(newRec);

            model.addAttribute("message", "Book record created successfully!");
            model.addAttribute("loanId", newId);

            return "records/add-record-success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "records/add-record";
        }
    }


    // SHOW EDIT FORM
    @GetMapping("/records/{loanId}/edit")
    public String showEditForm(@PathVariable int loanId, Model model) {
        BookRecord record = bookRecordDAO.findById(loanId);

        if (record == null) {
            model.addAttribute("error", "Book record not found with Loan ID: " + loanId);
            return "error";
        }

        model.addAttribute("record", record);
        return "records/edit-record";
    }

    // HANDLE EDIT
    @PostMapping("/records/{loanId}/edit")
    public String editRecord(
            @PathVariable int loanId,
            @RequestParam int copyID,
            @RequestParam int userID,
            @RequestParam String checkoutDate,
            @RequestParam String dueDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam int renewCount,
            Model model) {

        try {
            BookRecord existing = bookRecordDAO.findById(loanId);
            if (existing == null) {
                model.addAttribute("error", "Book record not found with Loan ID: " + loanId);
                return "error";
            }

            existing.setCopyID(copyID);
            existing.setUserID(userID);
            existing.setCheckoutDate(LocalDateTime.parse(checkoutDate));
            existing.setDueDate(LocalDateTime.parse(dueDate));
            existing.setReturnDate(returnDate == null || returnDate.isEmpty()
                    ? null
                    : LocalDateTime.parse(returnDate));
            existing.setRenewCount(renewCount);

            bookRecordDAO.update(existing);

            model.addAttribute("message", "Book record updated successfully!");
            model.addAttribute("record", existing);
            return "records/edit-record-success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "records/edit-record";
        }
    }

    // DELETE RECORD
    @PostMapping("/records/{loanId}/delete")
    public String deleteRecord(@PathVariable int loanId, Model model) {
        try {
            int rows = bookRecordDAO.delete(loanId);

            if (rows == 0) {
                model.addAttribute("error", "Unable to delete — record not found.");
                return "error";
            }

            model.addAttribute("message", "Record deleted successfully!");
            return "records/delete-record-success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
