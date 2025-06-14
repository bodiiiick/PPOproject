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

    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String title = parts[0];
                    String author = parts[1];
                    boolean isBorrowed = Boolean.parseBoolean(parts[2]);
                    String borrowedBy = parts[3].equals("null") ? null : parts[3];
                    books.add(new Book(title, author, isBorrowed, borrowedBy));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return books;
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String password = parts[1];
                    Role role = Role.valueOf(parts[2]);
                    List<String> borrowedBooks = new ArrayList<>();
                    if (parts.length > 3) {
                        for (int i = 3; i < parts.length; i++) {
                            borrowedBooks.add(parts[i]);
                        }
                    }
                    users.add(new User(username, password, role, borrowedBooks));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return users;
    }

    public void saveBooks(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books) {
                writer.write(String.format("%s,%s,%b,%s\n",
                        book.getTitle(),
                        book.getAuthor(),
                        book.isBorrowed(),
                        book.getBorrowedBy() != null ? book.getBorrowedBy() : "null"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                StringBuilder sb = new StringBuilder();
                sb.append(user.getUsername()).append(",");
                sb.append(user.getPassword()).append(",");
                sb.append(user.getRole());

                for (String book : user.getBorowedBooks()) {
                    sb.append(",").append(book);
                }

                writer.write(sb.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}