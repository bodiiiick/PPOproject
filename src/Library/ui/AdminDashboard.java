package Library.ui;

import Library.model.Book;
import Library.model.User;
import Library.model.Role;
import Library.service.AuthService;
import Library.service.LibraryService;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final AuthService authService;
    private final LibraryService libraryService;
    private final JList<User> usersList;
    private final JList<Book> booksList;
    private final DefaultListModel<User> usersModel;
    private final DefaultListModel<Book> booksModel;
    private final JTextArea userDetailsArea;

    public AdminDashboard(AuthService authService) {
        this.authService = authService;
        this.libraryService = authService.getLibraryService();

        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        usersModel = new DefaultListModel<>();
        booksModel = new DefaultListModel<>();

        usersList = new JList<>(usersModel);
        booksList = new JList<>(booksModel);
        booksList.setCellRenderer(new BookListRenderer());

        userDetailsArea = new JTextArea();
        userDetailsArea.setEditable(false);

        JButton addUserButton = new JButton("Add User");
        JButton removeUserButton = new JButton("Remove User");
        JButton addBookButton = new JButton("Add Book");
        JButton removeBookButton = new JButton("Remove Book");
        JButton logoutButton = new JButton("Logout");

        addUserButton.addActionListener(e -> addUser());
        removeUserButton.addActionListener(e -> removeUser());
        addBookButton.addActionListener(e -> addBook());
        removeBookButton.addActionListener(e -> removeBook());
        logoutButton.addActionListener(e -> logout());

        usersList.addListSelectionListener(this::userSelected);

        JPanel usersPanel = createListPanel("Users", usersList);
        JPanel booksPanel = createListPanel("Books", booksList);
        JPanel detailsPanel = createDetailsPanel("User Details", userDetailsArea);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.add(addUserButton);
        buttonPanel.add(removeUserButton);
        buttonPanel.add(addBookButton);
        buttonPanel.add(removeBookButton);
        buttonPanel.add(logoutButton);

        JSplitPane mainSplitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                usersPanel,
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        booksPanel,
                        detailsPanel
                )
        );
        mainSplitPane.setResizeWeight(0.25);

        add(mainSplitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateLists();
        setVisible(true);
    }

    private JPanel createListPanel(String title, JList<?> list) {
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

    private JPanel createDetailsPanel(String title, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP
        ));

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void updateLists() {
        usersModel.clear();
        booksModel.clear();

        libraryService.getAllUsers().forEach(usersModel::addElement);
        libraryService.getAllBooks().forEach(booksModel::addElement);
    }

    private void userSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        User selectedUser = usersList.getSelectedValue();
        if (selectedUser == null) return;

        List<Book> borrowedBooks = libraryService.getUserBooks(selectedUser);

        StringBuilder sb = new StringBuilder();
        sb.append("Username: ").append(selectedUser.getUsername()).append("\n");
        sb.append("Role: ").append(selectedUser.getRole()).append("\n\n");

        sb.append("=== Borrowed Books ===\n");
        if (borrowedBooks.isEmpty()) {
            sb.append("No books borrowed\n");
        } else {
            for (Book book : borrowedBooks) {
                sb.append("- ").append(book.getTitle()).append(" by ").append(book.getAuthor()).append("\n");
            }
        }

        userDetailsArea.setText(sb.toString());
    }

    private void addUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<Role> roleComboBox = new JComboBox<>(Role.values());

        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Role:", roleComboBox
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add New User",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            Role role = (Role) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (libraryService.findUser(username) != null) {
                JOptionPane.showMessageDialog(this, "User already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            libraryService.addUser(new User(username, password, role, new ArrayList<>()));
            updateLists();
            JOptionPane.showMessageDialog(this, "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeUser() {
        User selectedUser = usersList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete user: " + selectedUser.getUsername() + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            libraryService.getAllUsers().remove(selectedUser);
            updateLists();
            userDetailsArea.setText("");
            JOptionPane.showMessageDialog(this, "User removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addBook() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();

        Object[] fields = {
                "Title:", titleField,
                "Author:", authorField
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add New Book",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();

            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            libraryService.addBook(new Book(title, author, false, null));
            updateLists();
            JOptionPane.showMessageDialog(this, "Book added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeBook() {
        Book selectedBook = booksList.getSelectedValue();
        if (selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Please select a book", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete book: " + selectedBook.getTitle() + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            libraryService.getAllBooks().remove(selectedBook);
            updateLists();
            JOptionPane.showMessageDialog(this, "Book removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
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
                String status = book.isBorrowed() ? "[BORROWED]" : "[AVAILABLE]";
                setText(book.getTitle() + " by " + book.getAuthor() + " " + status);

                if (book.isBorrowed()) {
                    setForeground(Color.RED);
                } else {
                    setForeground(Color.BLACK);
                }
            }
            return this;
        }
    }
}