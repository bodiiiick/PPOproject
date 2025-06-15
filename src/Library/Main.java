package Library;

import Library.service.AuthService;
import Library.service.FileService;
import Library.service.LibraryService;
import Library.ui.LoginForm;
import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FileService fileService = new FileService();
                LibraryService libraryService = new LibraryService();
                AuthService authService = new AuthService(libraryService);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    fileService.saveBooks(libraryService.getAllBooks());
                    fileService.saveUsers(libraryService.getAllUsers());
                    System.out.println("Data saved successfully on exit");
                }));

                new LoginForm(authService);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Помилка запуску програми: " + e.getMessage(),
                        "Критична помилка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}