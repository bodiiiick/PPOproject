package Library.model;

public class Book {
    private String title;
    private String author;
    private boolean isBorrowed;
    private String borrowedBy;

    public Book(String title, String author, boolean isBorrowed, String borrowedBy) {
        this.title = title;
        this.author = author;
        this.isBorrowed = isBorrowed;
        this.borrowedBy = borrowedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public String getBorrowedBy() {
        return borrowedBy;
    }

    public void borrowBook(String username){
        this.isBorrowed = true;
        this.borrowedBy = username;
    }

    public void returnBook(){
        this.borrowedBy = null;
        this.isBorrowed = false;
    }
}
