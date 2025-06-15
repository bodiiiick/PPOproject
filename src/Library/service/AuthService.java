package Library.service;

import Library.model.Book;
import Library.model.User;
import Library.model.Role;
import java.util.List;

public class AuthService {
    private final LibraryService libraryService;
    private User currentUser;

    public AuthService(LibraryService libraryService) {
        this.libraryService = libraryService;
        FileService fileService = new FileService();

        List<User> loadedUsers = fileService.loadUsers();
        System.out.println("Users uploaded: " + loadedUsers.size());

        for (User user : loadedUsers) {
            this.libraryService.addUser(user);
        }

        List<Book> loadedBooks = fileService.loadBooks();
        System.out.println("Books downloaded: " + loadedBooks.size());

        for (Book book : loadedBooks) {
            this.libraryService.addBook(book);
        }

        System.out.println("Users in the service: " + libraryService.getAllUsers().size());
        System.out.println("Books in the service: " + libraryService.getAllBooks().size());
    }

    public User login(String username, String password) {
        System.out.println("\nLogin attempt for: " + username);
        System.out.println("Available users: " + libraryService.getAllUsers().size());

        User user = libraryService.findUser(username);

        if (user != null) {
            System.out.println("User found: " + user.getUsername());
            System.out.println("Expected password: " + user.getPassword());
            System.out.println("Password entered: " + password);

            if (user.getPassword().equals(password)) {
                currentUser = user;
                System.out.println("Login successful!");
                return user;
            } else {
                System.out.println("Login successful");
            }
        } else {
            System.out.println("User not found");
            System.out.println("Available logins: ");
            for (User u : libraryService.getAllUsers()) {
                System.out.println("- " + u.getUsername());
            }
        }

        return null;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("Exit: " + currentUser.getUsername());
        }
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    public LibraryService getLibraryService() {
        return libraryService;
    }
}