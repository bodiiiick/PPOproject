package Library;

import Library.service.AuthService;
import Library.service.FileService;
import Library.service.LibraryService;
import Library.ui.LoginForm;

public class Main {
    public static void main(String[] args) {
        // Initialize services
        LibraryService libraryService = new LibraryService();
        FileService fileService = new FileService();
        AuthService authService = new AuthService(libraryService);

        // Load data from files
        libraryService.getAllBooks().addAll(fileService.loadBooks());
        libraryService.getAllUsers().addAll(fileService.loadUsers());

        // Add shutdown hook to save data on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            fileService.saveBooks(libraryService.getAllBooks());
            fileService.saveUsers(libraryService.getAllUsers());
        }));

        // Start the application
        new LoginForm();
    }
}