package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.sjsu.library.models.Title;
import edu.sjsu.library.models.Copy;
import edu.sjsu.library.models.User;
import edu.sjsu.library.models.BookRecord;
import edu.sjsu.library.models.AvailabilitySummary;
import edu.sjsu.library.services.BookService;
import edu.sjsu.library.services.LoanService;
import edu.sjsu.library.services.UserService;
import edu.sjsu.library.utils.AuthorizationUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Year;
import java.util.List;

/**
 * TitleController handles catalog browsing, title management (staff), and loan record viewing.
 * - Staff can add, edit, delete titles and view all loan records
 * - Members can browse visible titles with filters and view their own loan records
 */
@Controller
@RequestMapping("/titles")
public class TitleController {
    private final BookService bookService;
    private final LoanService loanService;
    private final UserService userService;
    private final AuthorizationUtils authUtils;

    public TitleController(BookService bookService, LoanService loanService, 
                          UserService userService, AuthorizationUtils authUtils) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.userService = userService;
        this.authUtils = authUtils;
    }

    // ==================== CATALOG BROWSING ====================
    
    // GET /titles
    // List all titles with optional filters.
    @GetMapping
    public String listTitles(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly,
            HttpServletRequest request,
            Model model) {
        
        int requestorID = getRequestorId(request);
        User requestor = userService.findById(requestorID);
        
        // Start with all titles, then apply filters sequentially
        List<Title> titles = bookService.getAllTitles(requestorID);
        
        // Apply title filter if provided
        if (title != null && !title.isBlank()) {
            final String searchTitle = title.trim().toLowerCase();
            titles = titles.stream()
                .filter(t -> t.getTitle().toLowerCase().contains(searchTitle))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Apply author filter if provided
        if (author != null && !author.isBlank()) {
            final String searchAuthor = author.trim().toLowerCase();
            titles = titles.stream()
                .filter(t -> t.getAuthor().toLowerCase().contains(searchAuthor))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Apply genre filter if provided
        if (genre != null && !genre.isBlank()) {
            try {
                Title.Genre genreEnum = Title.Genre.valueOf(genre.trim().toUpperCase().replace(" ", "_"));
                final Title.Genre targetGenre = genreEnum;
                titles = titles.stream()
                    .filter(t -> t.getGenre() == targetGenre)
                    .collect(java.util.stream.Collectors.toList());
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Invalid genre: " + genre);
            }
        }
        
        // Apply year range filter if provided
        if (startYear != null || endYear != null) {
            final int start = startYear != null ? startYear : 1000;
            final int end = endYear != null ? endYear : Year.now().getValue();
            titles = titles.stream()
                .filter(t -> {
                    int year = t.getYearPublished().getValue();
                    return year >= start && year <= end;
                })
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Filter by availability if requested
        if (availableOnly) {
            titles = bookService.filterTitlesByAvailability(titles, true, requestorID);
        }

        // Preserve search parameters in model for form repopulation
        model.addAttribute("searchTitle", title);
        model.addAttribute("searchAuthor", author);
        model.addAttribute("searchGenre", genre);
        model.addAttribute("startYear", startYear);
        model.addAttribute("endYear", endYear);
        model.addAttribute("availableOnly", availableOnly);
        
        model.addAttribute("titles", titles);
        model.addAttribute("isStaff", requestor.isStaff());
        model.addAttribute("currentUser", requestor);
        model.addAttribute("genres", Title.Genre.values());
        
        return "titles"; // Resolves to src/main/resources/templates/titles.html
    }

    // GET /titles/{id}
    // View single title details with copies and availability.
    @GetMapping("/{id}")
    public String viewTitle(@PathVariable int id, HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        User requestor = userService.findById(requestorID);
        
        Title title = bookService.getTitleById(id, requestorID);
        if (title == null) {
            model.addAttribute("error", "Title not found or not accessible.");
            return "redirect:/titles";
        }
        
        List<Copy> copies = bookService.getCopiesForTitle(id, requestorID);
        AvailabilitySummary availability = bookService.getAvailabilitySummary(id, requestorID);
        
        model.addAttribute("title", title);
        model.addAttribute("copies", copies);
        model.addAttribute("availability", availability);
        model.addAttribute("isStaff", requestor.isStaff());
        model.addAttribute("currentUser", requestor);
        
        return "title-detail"; // Resolves to src/main/resources/templates/title-detail.html
    }

    // ==================== TITLE MANAGEMENT (STAFF ONLY) ====================
    
    // GET /titles/add
    // Show form to add new title (staff only).
    @GetMapping("/add")
    public String showAddTitleForm(HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        
        try {
            authUtils.validateStaffAccess(requestorID);
            User requestor = userService.findById(requestorID);
            
            model.addAttribute("genres", Title.Genre.values());
            model.addAttribute("isStaff", true);
            model.addAttribute("currentUser", requestor);
            
            return "title-add"; // Resolves to src/main/resources/templates/title-add.html
        } catch (Exception e) {
            model.addAttribute("error", "Access denied: " + e.getMessage());
            return "redirect:/titles";
        }
    }

    // POST /titles/add
    // Add new title to catalog (staff only).
    @PostMapping("/add")
    public String addTitle(
            @RequestParam String isbn,
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam int yearPublished,
            @RequestParam String genre,
            HttpServletRequest request,
            Model model) {
        
        int requestorID = getRequestorId(request);

        authUtils.validateStaffAccess(requestorID);
        
        // Preserve submitted values in case of error.
        model.addAttribute("draftIsbn", isbn);
        model.addAttribute("draftTitle", title);
        model.addAttribute("draftAuthor", author);
        model.addAttribute("draftYear", yearPublished);
        model.addAttribute("draftGenre", genre);

        try {
            Title.Genre genreEnum = Title.Genre.valueOf(genre.trim().toUpperCase());
            Title newTitle = bookService.addTitle(isbn.trim(), title.trim(), author.trim(),
                                                  Year.of(yearPublished), genreEnum, requestorID);

            return "redirect:/titles/" + newTitle.getTitleID();
        } catch (IllegalArgumentException | java.time.DateTimeException e) {
            model.addAttribute("error", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add title: " + e.getMessage());
        }

        model.addAttribute("genres", Title.Genre.values());
        model.addAttribute("isStaff", true);
        model.addAttribute("currentUser", userService.findById(requestorID));
        return "title-add";
    }

    // GET /titles/{id}/edit
    // Show form to edit title (staff only).
    @GetMapping("/{id}/edit")
    public String showEditTitleForm(@PathVariable int id, HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        
        try {
            authUtils.validateStaffAccess(requestorID);
            User requestor = userService.findById(requestorID);
            
            Title title = bookService.getTitleById(id, requestorID);
            if (title == null) {
                model.addAttribute("error", "Title not found.");
                return "redirect:/titles";
            }
            
            model.addAttribute("title", title);
            model.addAttribute("genres", Title.Genre.values());
            model.addAttribute("isStaff", true);
            model.addAttribute("currentUser", requestor);
            
            return "title-edit"; // Resolves to src/main/resources/templates/title-edit.html
        } catch (Exception e) {
            model.addAttribute("error", "Access denied: " + e.getMessage());
            return "redirect:/titles";
        }
    }

    // POST /titles/{id}/edit 
    // Update title details (staff only).
    @PostMapping("/{id}/edit")
    public String editTitle(
            @PathVariable int id,
            @RequestParam String isbn,
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam int yearPublished,
            @RequestParam String genre,
            @RequestParam(defaultValue = "true") boolean visible,
            HttpServletRequest request,
            Model model) {
        
        int requestorID = getRequestorId(request);
        
        try {
            Title.Genre genreEnum = Title.Genre.valueOf(genre.toUpperCase());
            bookService.editTitle(id, isbn, title, author, 
                                 Year.of(yearPublished), genreEnum, visible, requestorID);
            
            model.addAttribute("success", "Title updated successfully!");
            return "redirect:/titles/" + id;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update title: " + e.getMessage());
            Title existingTitle = bookService.getTitleById(id, requestorID);
            model.addAttribute("title", existingTitle);
            model.addAttribute("genres", Title.Genre.values());
            return "title-edit";
        }
    }

    // POST /titles/{id}/delete
    // Delete title from catalog (staff only).
    @PostMapping("/{id}/delete")
    public String deleteTitle(@PathVariable int id, HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        
        try {
            bookService.removeTitle(id, requestorID);
            model.addAttribute("success", "Title deleted successfully!");
            return "redirect:/titles";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete title: " + e.getMessage());
            return "redirect:/titles/" + id;
        }
    }

    // ==================== LOAN RECORDS ====================
    
    // GET /titles/loans 
    // View loan records (staff see all, members see own).
    @GetMapping("/loans")
    public String viewLoans(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer titleId,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly,
            HttpServletRequest request,
            Model model) {
        
        int requestorID = getRequestorId(request);
        User requestor = userService.findById(requestorID);
        
        List<BookRecord> loans;
        
        try {
            if (userId != null) {
                // View loans for specific user
                if (activeOnly) {
                    loans = loanService.getCurrentLoansByUser(requestorID, userId);
                } else {
                    loans = loanService.getLoanHistoryByUser(requestorID, userId);
                }
            } else if (titleId != null) {
                // View loans for specific title (staff only)
                authUtils.validateStaffAccess(requestorID);
                loans = loanService.getLoanHistoryByTitle(requestorID, titleId);
            } else {
                // Default: members see their own loans, staff need to specify filters
                if (requestor.isStaff()) {
                    model.addAttribute("info", "Please select a user or title to view loans.");
                    loans = List.of();
                } else {
                    loans = activeOnly ? 
                        loanService.getCurrentLoansByUser(requestorID, requestorID) :
                        loanService.getLoanHistoryByUser(requestorID, requestorID);
                }
            }
            
            model.addAttribute("loans", loans);
            model.addAttribute("isStaff", requestor.isStaff());
            model.addAttribute("currentUser", requestor);
            model.addAttribute("activeOnly", activeOnly);
            
            return "loans"; // Resolves to src/main/resources/templates/loans.html
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve loans: " + e.getMessage());
            model.addAttribute("loans", List.of());
            model.addAttribute("isStaff", requestor.isStaff());
            model.addAttribute("currentUser", requestor);
            return "loans";
        }
    }

    // GET /titles/loans/{loanId} 
    // View single loan record detail.
    @GetMapping("/loans/{loanId}")
    public String viewLoanDetail(@PathVariable int loanId, HttpServletRequest request, Model model) {
        int requestorID = getRequestorId(request);
        User requestor = userService.findById(requestorID);
        
        try {
            BookRecord loan = loanService.getLoanById(requestorID, loanId);
            
            if (loan == null) {
                model.addAttribute("error", "Loan record not found.");
                return "redirect:/titles/loans";
            }
            
            // Get additional information for display
            User borrower = userService.findById(loan.getUserID());
            // Note: To get title info, we need to get copy first, then title from copy
            // This could be improved by adding methods to get Title/Copy directly from loan
            
            model.addAttribute("loan", loan);
            model.addAttribute("borrower", borrower);
            model.addAttribute("isStaff", requestor.isStaff());
            model.addAttribute("currentUser", requestor);
            
            return "loan-detail"; // Resolves to src/main/resources/templates/loan-detail.html
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve loan details: " + e.getMessage());
            return "redirect:/titles/loans";
        }
    }

    // ==================== HELPER METHODS ====================
    
    private int getRequestorId(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("USER_ID");
        if (userId == null) {
            throw new IllegalStateException("No user logged in");
        }
        return userId;
    }
}
