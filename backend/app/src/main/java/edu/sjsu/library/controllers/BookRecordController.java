package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import edu.sjsu.library.models.BookRecord;
import edu.sjsu.library.services.LoanService;

@Controller
@RequestMapping("/api/loans")
public class BookRecordController {

    private final LoanService loanService;

    public BookRecordController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<BookRecord> checkoutBook(
            @RequestParam String barcode,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        BookRecord loan = loanService.checkoutBook(barcode, requestorID);
        return ResponseEntity.status(201).body(loan);
    }

    @PostMapping("/renew/{loanID}")
    @ResponseBody
    public ResponseEntity<BookRecord> renewLoan(
            @PathVariable int loanID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        BookRecord renewed = loanService.renewLoan(loanID, requestorID);
        return ResponseEntity.ok(renewed);
    }

    @PostMapping("/return/{loanID}")
    @ResponseBody
    public ResponseEntity<BookRecord> returnBook(
            @PathVariable int loanID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        BookRecord returned = loanService.returnBook(loanID, requestorID);
        return ResponseEntity.ok(returned);
    }

    @GetMapping("/user/{userID}")
    @ResponseBody
    public ResponseEntity<List<BookRecord>> getLoanHistoryByUser(
            @PathVariable int userID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        List<BookRecord> loans = loanService.getLoanHistoryByUser(requestorID, userID);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/user/{userID}/current")
    @ResponseBody
    public ResponseEntity<List<BookRecord>> getCurrentLoansByUser(
            @PathVariable int userID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        List<BookRecord> loans = loanService.getCurrentLoansByUser(requestorID, userID);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/title/{titleID}")
    @ResponseBody
    public ResponseEntity<List<BookRecord>> getLoanHistoryByTitle(
            @PathVariable int titleID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        List<BookRecord> loans = loanService.getLoanHistoryByTitle(requestorID, titleID);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/copy/{copyID}")
    @ResponseBody
    public ResponseEntity<List<BookRecord>> getLoanHistoryByCopy(
            @PathVariable int copyID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        List<BookRecord> loans = loanService.getLoanHistoryByCopy(requestorID, copyID);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/copy/{copyID}/current")
    @ResponseBody
    public ResponseEntity<BookRecord> getCurrentLoanByCopy(
            @PathVariable int copyID,
            HttpServletRequest request) {

        int requestorID = getRequestorId(request);
        BookRecord loan = loanService.getCurrentLoanByCopy(requestorID, copyID);
        return loan != null ? ResponseEntity.ok(loan) : ResponseEntity.notFound().build();
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
