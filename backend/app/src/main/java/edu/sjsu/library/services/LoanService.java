/*
    LoanService.java handles book loans (but NOT holds).
*/
package edu.sjsu.library.services;

import edu.sjsu.library.dao.BookRecordDAO;
import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.dao.FineDAO;
import edu.sjsu.library.dao.CopyDAO;
import edu.sjsu.library.dao.TitleDAO;

import edu.sjsu.library.models.User;
import edu.sjsu.library.models.BookRecord;
import edu.sjsu.library.models.Fine;
import edu.sjsu.library.models.Copy;
import edu.sjsu.library.models.Title;

import edu.sjsu.library.exceptions.BookAlreadyReturnedException;
import edu.sjsu.library.exceptions.AuthorizationFailedException;
import edu.sjsu.library.exceptions.CheckoutNotAllowedException;
import edu.sjsu.library.exceptions.ReturnNotAllowedException;
import edu.sjsu.library.exceptions.RenewalNotAllowedException;
import edu.sjsu.library.exceptions.AuthenticationFailedException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.time.temporal.ChronoUnit; 

@Service
@Transactional
public class LoanService {
    private final BookRecordDAO bookRecordDAO;
    private final UserDAO userDAO;
    private final FineDAO fineDAO;
    private final CopyDAO copyDAO;
    private final TitleDAO titleDAO;

    // Business rule constants (service-level policies):
    private static final int MAX_ACTIVE_LOANS = 5; // Users can checkout a limited number of books at a time.
    private static final BigDecimal MAX_OUTSTANDING_FINES = new BigDecimal("30.00");
    private static final BigDecimal OVERDUE_FINE = new BigDecimal("5.00");

    // Constructor:
    public LoanService(BookRecordDAO bookRecordDAO, UserDAO userDAO, TitleDAO bookDAO, FineDAO fineDAO, CopyDAO copyDAO) {
        this.bookRecordDAO = bookRecordDAO;
        this.userDAO = userDAO;
        this.titleDAO = titleDAO;
        this.fineDAO = fineDAO;
        this.copyDAO = copyDAO;
    }

    // Helper methods:
    // Check if user reached the maximum loan limit.
    private boolean hasMaxLoansReached(int userID) {
        List<BookRecord> activeLoans = bookRecordDAO.findActiveByUser(userID);
        return activeLoans.size() >= MAX_ACTIVE_LOANS;
    }

    // Check if user reached maximum fine limit.
    private boolean hasExcessiveOutstandingFines(int userId) {
        List<Fine> outstandingFines = fineDAO.findByUserAndStatus(userId, Fine.FineStatus.UNPAID);
        BigDecimal totalFines = outstandingFines.stream()
            .map(Fine::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalFines.compareTo(MAX_OUTSTANDING_FINES) > 0;
    }

    // Automatically generate an overdue fine.
    private void createOverdueFine(BookRecord loan) {
        long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
        BigDecimal fineAmount = OVERDUE_FINE;
        
        Fine overdueFine = new Fine(
            loan.getUserID(),
            loan.getLoanID(),
            fineAmount,
            "System generated fine: Overdue return - " + daysOverdue + " days late."
        );
        
        fineDAO.insert(overdueFine);
    }

    // 1. Book checkout (this is a MEMBER only service).
    // This needs to check the status of the user account as well as total oustanding fines to verify is they can checkout or not.
    public BookRecord checkoutBook(String barcode, int requestorID) {

        // Find the user who is requesting the checkout service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }

        // Find the copy of the book that is being checked out by barcode.
        Copy copy = copyDAO.findByBarcode(barcode);
        if (copy == null) {
            throw new IllegalArgumentException("No book found with barcode: " + barcode);
        }

        // Make sure their account is active.
        if (!requestor.isActive()) {
            throw new CheckoutNotAllowedException("Your account status is currently INACTIVE. Please visit staff to re-activate account to complete checkout.");
        }

        // Make sure sure their account is unrestricted.
        if (requestor.isRestricted()) {
            throw new CheckoutNotAllowedException("Your account is currently restricted. Please pay outstanding fines and see library staff to remove account restriction.");
        }

        // Make sure they are not checking out with large sums of unpaid fines.
        if (hasExcessiveOutstandingFines(requestorID)) {
            throw new CheckoutNotAllowedException("Outstanding fines exceed limit.");
        }

        // Make sure they are not STAFF.
        if (requestor.isStaff()) {
            throw new AuthorizationFailedException("Staff cannot checkout books for personal use. Please switch to a member account.");
        }

        // Make sure that the copy is actually available for checkout.
        if (!copy.isAvailable()) {
            throw new CheckoutNotAllowedException("Copy is not available for checkout. Please see staff for details.");
        }

        // Check to see if the user already hit their maximum checkout limit.
        if (hasMaxLoansReached(requestorID)) {
            throw new CheckoutNotAllowedException("Maximum loan limit reached.");
        }

        // Create the loan.
        BookRecord loan = new BookRecord(copy.getCopyID(), requestorID);

        // Persist to database.
        bookRecordDAO.insert(loan);

        // Update the copy status.
        copy.markCheckedOut();

        // Persist to database.
        copyDAO.update(copy);

        return loan;        
    }

    // 2. Book renewal.
    // STAFF can apply renewals beyond maximum renewals on behalf of MEMBERS.
    // MEMBERS cannot personally renew beyond maximum renewals.
    // In either case, we need to make sure the book is not on hold.
    public BookRecord renewLoan(int loanID, int requestorID) {
        // Find the user who is requesting the service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }

        // Find the loan record that is being renewed.
        BookRecord loan = bookRecordDAO.findById(loanID);
        if (loan == null) {
            throw new IllegalArgumentException("Loan record not found.");
        }

        // Check if the book is reserved.
        Copy copy = copyDAO.findById(loan.getCopyID());
        if (copy.isReserved()) {
            throw new RenewalNotAllowedException("Cannot renew: this book is currently reserved for another member.");
        }

        // Authorization concern: (see above).
        if (requestor.isStaff()) {
            // STAFF have full privileges, no need for further authorization checks.
        } else {
            if (loan.getUserID() != requestorID) {
                throw new AuthorizationFailedException("You can only renew your own loans.");
            }
            if (hasExcessiveOutstandingFines(loan.getUserID)) {
                throw new CheckoutNotAllowedException("Cannot renew with outstanding fines.");
            }
        }

        // STAFF need to manually bypass the renew limit check.
        if (requestor.isStaff() && loan.getRenewCount() >= BookRecord.getMaxRenewals()) {
            if (loan.getReturnDate() != null) {
                throw new RenewalNotAllowedException("Cannot renew: book already returned.");
            }

            loan.staffRenew();
        } else {
            loan.renew();
        }

        // Persist to DB.
        bookRecordDAO.update(loan);

        return loan;
    }

    // 3. Book return (MEMBER only function).
    public BookRecord returnBook(int loanID, int requestorID) {
        // Find the user who is requesting the return service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }

        // Find the correct loan record.
        BookRecord loan = bookRecordDAO.findById(loanID);
        if (loan == null) {
            throw new IllegalArgumentException("Loan record not found.");
        }

        // Authorization concern: only MEMBER accounts can return books.
        if (requestor.isStaff()) {
            throw new AuthorizationFailedException("Staff accounts cannot return books.");
        }

        // Members can only return their own books.
        if (loan.getUserID() != requestorID) {
            throw new AuthorizationFailedException("You can only return your own books.");
        }

        // Return the book.
        loan.returnBook();

        // Overdue fines are calculated after successful return!
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            createOverdueFine(loan);
        }

        // Persist changes to DB.
        bookRecordDAO.update(loan);

        // Update the copy information.
        Copy copy = copyDAO.findById(loan.getCopyID());
        copy.markAvailable();

        // Persist changes to DB.
        copyDAO.update(copy);

        return loan;
    }

    // 4. View all loan records by user.
    // STAFF can view the loan records by user.
    // MEMBERS can only view their own loan records.
    public List<BookRecord> getLoanHistoryByUser(int requestorID, int subjectUserID) {
        // Find the user who is requesting the service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }

        // Authorization logic.
        if (requestor.isStaff()) {
            return bookRecordDAO.findByUser(subjectUserID);
        } else {
            if (requestorID != subjectUserID) {
                throw new AuthorizationFailedException("You can only view your own loan records.");
            }

            return bookRecordDAO.findByUser(requestorID);
        }
    }

    // 5. View active loans by user.
    // STAFF can view the active records by user.
    // MEMBERS can only view their own active loans.
    public List<BookRecord> getCurrentLoansByUser(int requestorID, int subjectUserID) {
        // Find the user who is requesting the service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }
        
        // Authorization logic
        if (requestor.isStaff()) {
            // STAFF can view any user's current loans
            return bookRecordDAO.findActiveByUser(subjectUserID);
        } else {
            // MEMBERS can only view their own current loans
            if (requestorID != subjectUserID) {
                throw new AuthorizationFailedException("You can only view your own loan records.");
            }
            return bookRecordDAO.findActiveByUser(requestorID);
        }
    }

    // 5. View loan record by book title. This is a STAFF only function.
    public List<BookRecord> getLoanHistoryByTitle(int requestorID, int titleID) {
        // Find the user who is requesting the service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }
        
        // Only staff can view loan records by title.
        if (!requestor.isStaff()) {
            throw new AuthorizationFailedException("Staff access required to view loan records by book.");
        }
        
        // This would find all copies of the title, then get loans for all those copies.
        List<Copy> titleCopies = copyDAO.findByTitle(titleID);
        List<BookRecord> allLoans = new ArrayList<>();
        
        for (Copy copy : titleCopies) {
            List<BookRecord> copyLoans = bookRecordDAO.findByCopy(copy.getCopyID());
            allLoans.addAll(copyLoans);
        }
        
        return allLoans;
    }

    // 6. View loan record by specific copy. This is a STAFF only function.
    public List<BookRecord> getLoanHistoryByCopy(int requestorID, int copyID) {
        // Find the user who is requesting the service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }
        
        // Only staff can view loan records by copy.
        if (!requestor.isStaff()) {
            throw new AuthorizationFailedException("Staff access required to view loan records by copy.");
        }
        
        // Verify the copy exists.
        Copy copy = copyDAO.findById(copyID);
        if (copy == null) {
            throw new IllegalArgumentException("Copy not found with ID: " + copyID);
        }
        
        // Get all loan records for this specific copy
        return bookRecordDAO.findByCopy(copyID);
    }

    // 7. Get current loan for a specific copy. This is a STAFF only function.
    public BookRecord getCurrentLoanByCopy(int requestorID, int copyID) {
        // Find the user who is requesting the service.
        User requestor = userDAO.findById(requestorID);
        if (requestor == null) {
            throw new AuthenticationFailedException("User not found.");
        }
        
        // Only staff can view current loan by copy.
        if (!requestor.isStaff()) {
            throw new AuthorizationFailedException("Staff access required to view current loan by copy.");
        }
        
        // Get the current active loan for this copy (if any)
        return bookRecordDAO.findActiveByCopy(copyID);
    }
}
