package edu.sjsu.library.models;
import java.time.Year;

public class Title {
    private int titleID;
    private String ISBN;
    private String title;
    private String author;
    private Year yearPublished;
    public enum Genre { // Just a common list of genres.
        FICTION,
        NON_FICTION,
        SCIENCE_FICTION,
        FANTASY,
        MYSTERY,
        THRILLER,
        ROMANCE,
        HISTORY,
        BIOGRAPHY,
        CHILDRENS,
        YOUNG_ADULT,
        POETRY,
        REFERENCE,
        SELF_HELP,
        SCIENCE,
        TECHNOLOGY,
        ART,
        TRAVEL,
        RELIGION,
        PHILOSOPHY,
        COMICS,
        COOKING;
    };
    private Genre genre;
    private boolean isVisible;

    // Constructor for new book titles (database auto-increments, titles are visible by default).
    public Title(String ISBN, String title, String author, Year yearPublished, Genre genre) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
        this.genre = genre;
        this.isVisible = true;
    }

    // Constructor for existing book titles (loaded from database).
    public Title(int titleID, String ISBN, String title, String author, Year yearPublished, Genre genre, boolean isVisible) {
        this.titleID = titleID;
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
        this.genre = genre;
        this.isVisible = isVisible;
    }
    
    // Getters:
    public int getTitleID() { return titleID; }
    public String getISBN() { return ISBN;}
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Year getYearPublished() { return yearPublished; }
    public Genre getGenre() { return genre; }
    public boolean isVisible() { return isVisible; }

    // Setters:
    public void setISBN(String ISBN) { this.ISBN = ISBN; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYearPublished(Year yearPublished) { this.yearPublished = yearPublished; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public void setVisible(boolean visible) { this.isVisible = visible; }
}
