package Library.ui;

import Library.model.Book;
import Library.model.User;
import Library.service.AuthService;
import Library.service.LibraryService;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class UserDashboard extends JFrame {
    private final AuthService authService;
    private final LibraryService libraryService;
    private final JList<Book> availableBooksList;
    private final JList<Book> borrowedBooksList;
    private final DefaultListModel<Book> availableBooksModel;
    private final DefaultListModel<Book> borrowedBooksModel;

    public UserDashboard(AuthService authService) {
        this.authService = authService;
        this.libraryService = authService.getLibraryService();

        setTitle("User Dashboard - " + authService.getCurrentUser().getUsername());
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        availableBooksModel = new DefaultListModel<>();
        borrowedBooksModel = new DefaultListModel<>();

        availableBooksList = new JList<>(availableBooksModel);
        availableBooksList.setCellRenderer(new BookListRenderer());
        borrowedBooksList = new JList<>(borrowedBooksModel);
        borrowedBooksList.setCellRenderer(new BookListRenderer());

        JPanel availablePanel = createBookListPanel("Available Books", availableBooksList);
        JPanel borrowedPanel = createBookListPanel("Your Borrowed Books", borrowedBooksList);

        JButton borrowButton = new JButton("Borrow Selected");
        JButton returnButton = new JButton("Return Selected");
        JButton logoutButton = new JButton("Logout");

        borrowButton.addActionListener(this::borrowSelectedBook);
        returnButton.addActionListener(this::returnSelectedBook);
        logoutButton.addActionListener(e -> logout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(borrowButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(logoutButton);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                availablePanel,
                borrowedPanel
        );
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateBookLists();
        setVisible(true);
    }

    private JPanel createBookListPanel(String title, JList<Book> list) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP
        ));

        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void borrowSelectedBook(ActionEvent e) {
        Book selectedBook = availableBooksList.getSelectedValue();
        if (selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Please select a book to borrow", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = libraryService.borrowBook(
                authService.getCurrentUser(),
                selectedBook.getTitle()
        );

        if (success) {
            updateBookLists();
            JOptionPane.showMessageDialog(this,
                    "Successfully borrowed: " + selectedBook.getTitle(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to borrow: " + selectedBook.getTitle(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnSelectedBook(ActionEvent e) {
        Book selectedBook = borrowedBooksList.getSelectedValue();
        if (selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Please select a book to return", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = libraryService.returnBook(
                authService.getCurrentUser(),
                selectedBook.getTitle()
        );

        if (success) {
            updateBookLists();
            JOptionPane.showMessageDialog(this,
                    "Successfully returned: " + selectedBook.getTitle(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to return: " + selectedBook.getTitle(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBookLists() {
        availableBooksModel.clear();
        borrowedBooksModel.clear();

        List<Book> allBooks = libraryService.getAllBooks();

        List<Book> availableBooks = allBooks.stream()
                .filter(book -> !book.isBorrowed())
                .collect(Collectors.toList());

        List<Book> borrowedBooks = libraryService.getUserBooks(authService.getCurrentUser());

        availableBooks.forEach(availableBooksModel::addElement);
        borrowedBooks.forEach(borrowedBooksModel::addElement);
    }

    private void logout() {
        authService.logout();
        dispose();
        new LoginForm(authService);
    }

    private static class BookListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Book) {
                Book book = (Book) value;
                setText(book.getTitle() + " by " + book.getAuthor());
            }
            return this;
        }
    }
}