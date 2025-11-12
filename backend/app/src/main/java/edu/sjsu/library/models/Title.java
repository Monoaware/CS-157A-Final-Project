package edu.sjsu.library.models;

public class Title {
    private int titleID;
    private String ISBN;
    private String title;
    private String author;
    private int yearPublished;
    private String genre;
    private boolean isVisible;

    public Title() {}
    
    public Title(int titleID, String ISBN, String title, String author, int yearPublished, String genre, boolean isVisible) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
        this.genre = genre;
        this.isVisible = isVisible;
    }

    public Title(String ISBN, String title, String author, int yearPublished, String genre, boolean isVisible) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
        this.genre = genre;
        this.isVisible = isVisible;
    }
    
    public int getTitleID() { return titleID; }
    public String getISBN() { return ISBN;}
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYearPublished() { return yearPublished; }
    public String getGenre() { return genre; }
    public boolean isVisible() { return isVisible; }

    public void setTitleID(int titleID) { this.titleID = titleID; }
    public void setISBN(String ISBN) { this.ISBN = ISBN; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYearPublished(int yearPublished) { this.yearPublished = yearPublished; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setVisible(boolean visible) { isVisible = visible; }
}
