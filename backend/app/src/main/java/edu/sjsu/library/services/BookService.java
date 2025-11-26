/*
    BookService.java handles the actual book catalogue.
*/
package edu.sjsu.library.services;

import edu.sjsu.library.utils.AuthorizationUtils;
import edu.sjsu.library.models.AvailabilitySummary;
import edu.sjsu.library.models.Copy;
import java.util.stream.Collectors;
import java.util.List;
import java.time.Year;
import edu.sjsu.library.dao.TitleDAO;
import edu.sjsu.library.dao.CopyDAO;
import edu.sjsu.library.models.Title;
import edu.sjsu.library.models.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {

    private final TitleDAO titleDAO;
    private final CopyDAO copyDAO;
    private final AuthorizationUtils authUtils;

    // Constructor:
    public BookService(TitleDAO titleDAO, CopyDAO copyDAO, AuthorizationUtils authUtils) {
        this.titleDAO = titleDAO;
        this.copyDAO = copyDAO;
        this.authUtils = authUtils;
    }

    // Helper methods:
    // Check if a user can view a title.
    private boolean canUserViewTitle(Title title, int requestorID) {
        User requestor = authUtils.getRequestor(requestorID);
        
        // Staff can view all titles
        if (requestor.isStaff()) {
            return true;
        }
        
        // Members can only view visible titles
        return title.isVisible();
    }

    // 1. Add a new title to the catalogue (STAFF ONLY).
    public Title addTitle(String ISBN, String title, String author, Year yearPublished, Title.Genre genre, int requestorID) {
        
        // Verify staff authorization.
        authUtils.validateStaffAccess(requestorID);

        // Check if the title already exists in our system by ISBN.
        if (titleDAO.findByIsbn(ISBN) != null) {
            throw new IllegalArgumentException("Title with ISBN " + ISBN + " already exists in the catalogue."); 
        }

        // Create new title.
        Title newTitle = new Title(ISBN, title, author, yearPublished, genre);

        // Insert into DB.
        int titleID = titleDAO.insert(newTitle);

        // Return the created title.
        return titleDAO.findById(titleID);
    }

    // 2. Remove a title from the catalogue (STAFF ONLY).
    public boolean removeTitle(int titleID, int requestorID) {

        // Verify staff authorization.
        authUtils.validateStaffAccess(requestorID);

        // Check if the title exists in our collection.
        Title title = titleDAO.findById(titleID);
        if (title == null) {
            throw new IllegalArgumentException("Title not found with ID: " + titleID);
        }

        // Return the deletion result.
        int affectedRows = titleDAO.delete(titleID);
        return affectedRows > 0;
    }

    // 3. Edit title details (STAFF ONLY). 
    public Title editTitle(int titleID, String ISBN, String title, String author, Year yearPublished, Title.Genre genre, boolean visibility, int requestorID) {
        
        // Verify staff authorization.
        authUtils.validateStaffAccess(requestorID);

        // Check if the title exists in our collection.
        Title existingTitle = titleDAO.findById(titleID);
        if (existingTitle == null) {
            throw new IllegalArgumentException("Title not found with ID: " + titleID);
        }

        // Update editable fields.
        existingTitle.setISBN(ISBN);
        existingTitle.setTitle(title);
        existingTitle.setAuthor(author);
        existingTitle.setYearPublished(yearPublished);
        existingTitle.setGenre(genre);
        existingTitle.setVisible(visibility);

        // Save changes to DB.
        titleDAO.update(existingTitle);

        return existingTitle;
    }

    // 4. Get title information for viewing/editing (MEMBERS & STAFF can use this).
    public Title getTitleById(int titleID, int requestorID) {
        
        // Confirm user is authenticated.
        authUtils.validateUserAccess(requestorID);

        // Get the title.
        Title title = titleDAO.findById(titleID);
        if (title == null) {
            return null;
        }

        // Check if user can view this title.
        if (!canUserViewTitle(title, requestorID)) {
            throw new IllegalArgumentException("Title not found or not accessible.");
        }

        // Return title information.
        return titleDAO.findById(titleID);
    }


    // 5. Search titles by ISBN (STAFF & MEMBERS can use this).
    public Title findByISBN(String ISBN, int requestorID) {
        
        // Confirm user is authenticated.
        authUtils.validateUserAccess(requestorID);

        // Get the title.
        Title title = titleDAO.findByIsbn(ISBN);
        if (title == null) {
            return null;
        }

        // Check if user can view this title.
        if (!canUserViewTitle(title, requestorID)) {
            return null; // Return null instead of throwing exception for search.
        }

        // Return title information.
        return title;
    }

    // 6. Search titles by author (STAFF & MEMBERS can use this).
    public List<Title> searchByAuthor(String author, int requestorID) {
        
        // Confirm user is authenticated and get user details.
        User requestor = authUtils.getRequestor(requestorID);
        
        // Find by author.
        List<Title> titles = titleDAO.findByAuthor(author);
        
        // Filter view by role.
        if (requestor.isStaff()) {
            return titles; 
        } else {
            return titles.stream()
                    .filter(Title::isVisible)
                    .collect(Collectors.toList());
        }
    }

    // 7. Search titles by genre (STAFF & MEMBERS can use this)
    public List<Title> searchByGenre(Title.Genre genre, int requestorID) {

        // Confirm user is authenticated and get user details.
        User requestor = authUtils.getRequestor(requestorID);
        
        // Find by genre.
        List<Title> titles = titleDAO.findByGenre(genre);
        
        // Filter view by role.
        if (requestor.isStaff()) {
            return titles; 
        } else {
            return titles.stream()
                    .filter(Title::isVisible)
                    .collect(Collectors.toList());
        }
    }

    // 8. Search titles by year range (STAFF & MEMBERS can use this).
    public List<Title> searchByYearRange(Year startYear, Year endYear, int requestorID) {

        // Confirm user is authenticated and get user details.
        User requestor = authUtils.getRequestor(requestorID);
        
        // Find by year range.
        List<Title> titles = titleDAO.findByYearRange(startYear, endYear);
        
        // Filter view by role.
        if (requestor.isStaff()) {
            return titles; 
        } else {
            return titles.stream()
                    .filter(Title::isVisible)
                    .collect(Collectors.toList());
        }
    }

    // 9. List all titles in catalogue (MEMBERS & STAFF can use this).
    public List<Title> getAllTitles(int requestorID) {
        
        // Confirm user is authenticated and get user details.
        User requestor = authUtils.getRequestor(requestorID);

        // Filter view by role.
        if (requestor.isStaff()) {
            return titleDAO.findAll();
        } else {
            return titleDAO.findAllVisible();
        }
    }

    // 10. Get copies for a specific title (shows physical availability).
    public List<Copy> getCopiesForTitle(int titleID, int requestorID) {

        // Confirm user is authenticated and get user details.
        User requestor = authUtils.getRequestor(requestorID);
        
        // Check if title exists and if user can view it.
        Title title = titleDAO.findById(titleID);
        if (title == null || !canUserViewTitle(title, requestorID)) {
            throw new IllegalArgumentException("Title not found or not accessible.");
        }
        
        // Find all copies for title.
        List<Copy> copies = copyDAO.findByTitle(titleID);
        
        // Filter copies based on user role.
        if (!requestor.isStaff()) {
            copies = copies.stream()
                    .filter(Copy::isVisible)
                    .collect(Collectors.toList());
        }
        
        return copies;
    }

    // 11. Get available copies for a specific title.
    public List<Copy> getAvailableCopiesForTitle(int titleID, int requestorID) {

        // Get all copies first.
        List<Copy> allCopies = getCopiesForTitle(titleID, requestorID);
        
        // Return the available ones.
        return allCopies.stream()
                .filter(copy -> copy.getStatus() == Copy.CopyStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    // 12. Filter titles by availability (use after search results)
    public List<Title> filterTitlesByAvailability(List<Title> titles, boolean availableOnly, int requestorID) {

        // Return all titles if no availability filter.
        if (!availableOnly) {
            return titles; 
        }
        
        // Return by filter.
        return titles.stream()
                .filter(title -> {
                    List<Copy> availableCopies = getAvailableCopiesForTitle(title.getTitleID(), requestorID);
                    return !availableCopies.isEmpty(); // Has at least one available copy
                })
                .collect(Collectors.toList());
    }

    // 13. Get availability summary for a title
    public AvailabilitySummary getAvailabilitySummary(int titleID, int requestorID) {

        // Get all copies for a title.
        List<Copy> copies = getCopiesForTitle(titleID, requestorID);
        
        // Calculate statistics...
        int total = copies.size();
        int available = (int) copies.stream()
                .filter(copy -> copy.getStatus() == Copy.CopyStatus.AVAILABLE)
                .count();
        int checkedOut = (int) copies.stream()
                .filter(copy -> copy.getStatus() == Copy.CopyStatus.CHECKED_OUT)
                .count();
        int other = total - available - checkedOut; // Lost, damaged, maintenance, reserved.
        
        return new AvailabilitySummary(total, available, checkedOut, other);
    }
}