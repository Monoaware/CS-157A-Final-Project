package edu.sjsu.library.services;

import edu.sjsu.library.dao.CopyDAO;
import edu.sjsu.library.dao.TitleDAO;
import edu.sjsu.library.dao.BookRecordDAO;
import edu.sjsu.library.dao.HoldDAO;
import edu.sjsu.library.models.Copy;
import edu.sjsu.library.models.Title;
import edu.sjsu.library.models.User;
import edu.sjsu.library.models.BookRecord;
import edu.sjsu.library.models.Hold;
import edu.sjsu.library.utils.AuthorizationUtils;
import edu.sjsu.library.exceptions.AuthorizationFailedException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CopyService {

    private final CopyDAO copyDAO;
    private final TitleDAO titleDAO;
    private final BookRecordDAO bookRecordDAO;
    private final HoldDAO holdDAO;
    private final AuthorizationUtils authUtils;

    public CopyService(CopyDAO copyDAO, TitleDAO titleDAO, BookRecordDAO bookRecordDAO, HoldDAO holdDAO, AuthorizationUtils authUtils) {
        this.copyDAO = copyDAO;
        this.titleDAO = titleDAO;
        this.bookRecordDAO = bookRecordDAO;
        this.holdDAO = holdDAO;
        this.authUtils = authUtils;
    }

    // 1. Get all copies (STAFF only)
    public List<Copy> getAllCopies(int requestorID) {
        authUtils.validateStaffAccess(requestorID);
        return copyDAO.findAll();
    }

    // 2. Get copy by ID (STAFF can view any, MEMBERS cannot)
    public Copy getCopyById(int copyID, int requestorID) {
        authUtils.validateStaffAccess(requestorID); // only staff can query individual copies
        return copyDAO.findById(copyID);
    }

    // 3. Get all copies for a title (STAFF can view any, MEMBERS can view visible copies only)
    public List<Copy> getCopiesForTitle(int titleID, int requestorID) {
        User requestor = authUtils.getRequestor(requestorID);
        List<Copy> copies = copyDAO.findByTitle(titleID);
        if (!requestor.isStaff()) {
            // Filter out non-visible copies for members
            copies = copies.stream().filter(Copy::isVisible).collect(Collectors.toList());
        }
        return copies;
    }

    // 4. Add a new copy (STAFF only)
    public Copy addCopy(Copy newCopy, int requestorID) {
        authUtils.validateStaffAccess(requestorID);

        // Validate that the title exists
        Title title = titleDAO.findById(newCopy.getTitleID());
        if (title == null) {
            throw new IllegalArgumentException("Cannot add copy: Title does not exist.");
        }

        int copyID = copyDAO.insert(newCopy);
        return copyDAO.findById(copyID);
    }

    // 5. Update an existing copy (STAFF only)
    public Copy updateCopy(Copy updatedCopy, int requestorID) {
        authUtils.validateStaffAccess(requestorID);

        Copy existing = copyDAO.findById(updatedCopy.getCopyID());
        if (existing == null) {
            throw new IllegalArgumentException("Copy not found with ID: " + updatedCopy.getCopyID());
        }

        copyDAO.update(updatedCopy);
        return copyDAO.findById(updatedCopy.getCopyID());
    }

    // 6. Delete a copy (STAFF only)
    public boolean deleteCopy(int copyID, int requestorID) {
        authUtils.validateStaffAccess(requestorID);

        Copy existing = copyDAO.findById(copyID);
        if (existing == null) {
            throw new IllegalArgumentException("Copy not found with ID: " + copyID);
        }

        // Check if there are any active holds (QUEUED or READY) referencing this specific copy
        // Note: Expired/Cancelled holds should have copyid = NULL, so they won't block deletion
        Hold activeHold = holdDAO.findActiveHoldByCopy(copyID);
        if (activeHold != null) {
            throw new IllegalStateException(
                "Cannot delete copy: it has an active hold (Hold ID: " + activeHold.getHoldID() + ", Status: " + activeHold.getStatus() + "). " +
                "Cancel or fulfill the hold before deleting the copy."
            );
        }

        // Check if there are any active loans (unreturned) for this copy
        BookRecord activeLoan = bookRecordDAO.findActiveByCopy(copyID);
        if (activeLoan != null) {
            throw new IllegalStateException(
                "Cannot delete copy: it has an active loan (Loan ID: " + activeLoan.getLoanID() + "). " +
                "The copy must be returned before it can be deleted."
            );
        }

        // Prevent deletion of copies that are in use or problematic
        Copy.CopyStatus status = existing.getStatus();
        if (status == Copy.CopyStatus.CHECKED_OUT || 
            status == Copy.CopyStatus.LOST || 
            status == Copy.CopyStatus.RESERVED) {
            throw new IllegalStateException(
                "Cannot delete copy: status is " + status + ". " +
                "Only AVAILABLE, DAMAGED, or MAINTENANCE copies can be deleted."
            );
        }

        int rowsAffected = copyDAO.delete(copyID);
        return rowsAffected > 0;
    }

    // 7. Check if copy is available
    public boolean isCopyAvailable(int copyID, int requestorID) {
        User requestor = authUtils.getRequestor(requestorID);
        Copy copy = copyDAO.findById(copyID);
        if (copy == null) return false;

        // Members can only query visible copies
        if (!requestor.isStaff() && !copy.isVisible()) {
            throw new AuthorizationFailedException("You cannot view this copy.");
        }

        return copy.getStatus() == Copy.CopyStatus.AVAILABLE;
    }

    // 8. Change status of a copy (STAFF only)
    public Copy changeCopyStatus(int copyID, Copy.CopyStatus newStatus, int requestorID) {
        authUtils.validateStaffAccess(requestorID);

        Copy copy = copyDAO.findById(copyID);
        if (copy == null) {
            throw new IllegalArgumentException("Copy not found with ID: " + copyID);
        }

        copy.setStatus(newStatus);
        copyDAO.update(copy);
        return copy;
    }

    // 9. Make copy visible or hidden (STAFF only)
    public Copy setCopyVisibility(int copyID, boolean visible, int requestorID) {
        authUtils.validateStaffAccess(requestorID);

        Copy copy = copyDAO.findById(copyID);
        if (copy == null) {
            throw new IllegalArgumentException("Copy not found with ID: " + copyID);
        }

        copy.setVisible(visible);
        copyDAO.update(copy);
        return copy;
    }
}