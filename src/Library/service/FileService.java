package Library.service;

import Library.model.Book;
import Library.model.User;
import Library.model.Role;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    private static final String BOOKS_FILE = "books.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String DELIMITER = "|";
    private static final String BOOK_SEPARATOR = ";;";
    private static final String NULL_MARKER = "\\NULL";

    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);
        if (!file.exists()) {
            try {
                createDefaultBooks();
            } catch (IOException ignored) {}
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER, -1);
                if (parts.length != 4) continue;
                String title = unescape(parts[0]);
                String author = unescape(parts[1]);
                boolean isBorrowed = Boolean.parseBoolean(parts[2]);
                String borrowedBy = NULL_MARKER.equals(parts[3]) ? null : unescape(parts[3]);
                books.add(new Book(title, author, isBorrowed, borrowedBy));
            }
        } catch (IOException ignored) {}
        return books;
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            try {
                createDefaultUsers();
            } catch (IOException ignored) {}
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER, -1);
                if (parts.length < 3) continue;
                String username = unescape(parts[0]);
                String password = unescape(parts[1]);
                Role role = Role.valueOf(parts[2]);
                List<String> borrowedBooks = new ArrayList<>();
                if (parts.length > 3) {
                    String[] books = parts[3].split(BOOK_SEPARATOR);
                    for (String book : books) {
                        if (!book.isEmpty() && !NULL_MARKER.equals(book)) {
                            borrowedBooks.add(unescape(book));
                        }
                    }
                }
                users.add(new User(username, password, role, borrowedBooks));
            }
        } catch (IOException ignored) {}
        return users;
    }

    private void createDefaultBooks() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            writer.write("The Great Gatsby|F. Scott Fitzgerald|false|\\\\NULL\n");
            writer.write("To Kill a Mockingbird|Harper Lee|false|\\\\NULL\n");
            writer.write("1984|George Orwell|false|\\\\NULL\n");
        }
    }

    private void createDefaultUsers() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            writer.write("admin|admin123|ADMIN|\n");
            writer.write("user1|password123|USER|\n");
            writer.write("user2|password123|USER|\n");
        }
    }

    public void saveBooks(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books) {
                String line = escape(book.getTitle()) + DELIMITER +
                        escape(book.getAuthor()) + DELIMITER +
                        book.isBorrowed() + DELIMITER +
                        (book.getBorrowedBy() != null ? escape(book.getBorrowedBy()) : NULL_MARKER);
                writer.write(line + "\n");
            }
        } catch (IOException ignored) {}
    }

    public void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                StringBuilder sb = new StringBuilder();
                sb.append(escape(user.getUsername())).append(DELIMITER);
                sb.append(escape(user.getPassword())).append(DELIMITER);
                sb.append(user.getRole()).append(DELIMITER);
                if (!user.getBorowedBooks().isEmpty()) {
                    for (int i = 0; i < user.getBorowedBooks().size(); i++) {
                        if (i > 0) sb.append(BOOK_SEPARATOR);
                        sb.append(escape(user.getBorowedBooks().get(i)));
                    }
                }
                writer.write(sb.toString() + "\n");
            }
        } catch (IOException ignored) {}
    }

    private String escape(String input) {
        if (input == null) return NULL_MARKER;
        return input.replace("\\", "\\\\")
                .replace(DELIMITER, "\\" + DELIMITER)
                .replace(BOOK_SEPARATOR, "\\" + BOOK_SEPARATOR)
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String unescape(String input) {
        if (input == null || NULL_MARKER.equals(input)) return null;
        return input.replace("\\" + DELIMITER, DELIMITER)
                .replace("\\" + BOOK_SEPARATOR, BOOK_SEPARATOR)
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
    }
}
