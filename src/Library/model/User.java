package Library.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private Role role;
    private List<String> borrowedBooks;

    public User(String username, String password, Role role, List<String> borrowedBooks) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.borrowedBooks = borrowedBooks != null ? borrowedBooks : new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public List<String> getBorowedBooks() {
        return borrowedBooks;
    }

    public void borrowBook(String bookTitle){
        borrowedBooks.add(bookTitle);
    }

    public void returnBook(String bookTitle) {
        borrowedBooks.removeIf(title -> title.equals(bookTitle));
    }
}
