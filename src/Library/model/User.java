package Library.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private Role role;
    private List<String> borowedBooks;

    public User(String username, String password, Role role, List<String> borowedBooks) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.borowedBooks = new ArrayList<>();
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
        return borowedBooks;
    }

    public void borrowBook(String bookTitle){
        borowedBooks.add(bookTitle);
    }

    public void returnBook(String bookTitle){
        borowedBooks.remove(bookTitle);
    }
}
