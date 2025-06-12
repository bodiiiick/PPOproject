package Library.service;

import Library.model.User;
import Library.model.Role;

public class AuthService {
    private final LibraryService libraryService;
    private User currentUser;

    public AuthService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    public User login(String username, String password) {
        User user = libraryService.findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }
}
