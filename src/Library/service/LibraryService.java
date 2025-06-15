package Library.service;

import Library.model.Book;
import Library.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LibraryService {
    private final List<Book> books = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public boolean removeBook(Book book) {
        if (book.isBorrowed()) {
            User borrower = findUser(book.getBorrowedBy());
            if (borrower != null) {
                borrower.returnBook(book.getTitle());
            }
            book.returnBook();
        }
        return books.remove(book);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public boolean removeUser(User user) {
        List<String> borrowedBooks = new ArrayList<>(user.getBorowedBooks());
        for (String bookTitle : borrowedBooks) {
            returnBook(user, bookTitle);
        }
        return users.remove(user);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    private Book findAvailableBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title) && !book.isBorrowed()) {
                return book;
            }
        }
        return null;
    }

    public boolean borrowBook(User user, String bookTitle) {
        Book book = findAvailableBook(bookTitle);
        if (book != null) {
            book.borrowBook(user.getUsername());
            user.borrowBook(book.getTitle());
            return true;
        }
        return false;
    }

    public boolean returnBook(User user, String bookTitle) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(bookTitle) &&
                    book.isBorrowed() &&
                    user.getUsername().equals(book.getBorrowedBy())) {
                book.returnBook();
                user.returnBook(bookTitle);
                return true;
            }
        }
        return false;
    }

    public List<Book> getUserBooks(User user) {
        List<Book> userBooks = new ArrayList<>();
        for (Book book : books) {
            if (user.getUsername().equals(book.getBorrowedBy())) {
                userBooks.add(book);
            }
        }
        return userBooks;
    }
}