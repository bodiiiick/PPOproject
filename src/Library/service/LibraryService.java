package Library.service;

import Library.model.Book;
import Library.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LibraryService {
    private final List<Book> books;
    private final List<User> users;

    public LibraryService() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
    }


    public void addBook(Book book) {
        books.add(book);
    }

    public void addUser(User user) {
        users.add(user);
    }


    public void removeBook(Book book) {
        books.removeIf(b ->
                Objects.equals(b.getTitle(), book.getTitle()) &&
                        Objects.equals(b.getAuthor(), book.getAuthor())
        );
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }


    public boolean borrowBook(String username, String bookTitle) {
        User user = findUser(username);
        if (user == null) return false;

        for (Book book : books) {
            if (book.getTitle().equals(bookTitle) && !book.isBorrowed()) {
                book.borrowBook(username);
                user.borrowBook(bookTitle);
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(String username, String bookTitle) {
        User user = findUser(username);
        if (user == null) return false;

        for (Book book : books) {
            if (book.getTitle().equals(bookTitle) &&
                    book.isBorrowed() &&
                    username.equals(book.getBorrowedBy())) {
                book.returnBook();
                user.returnBook(bookTitle);
                return true;
            }
        }
        return false;
    }

    public List<Book> getUserBooks(String username) {
        List<Book> userBooks = new ArrayList<>();
        for (Book book : books) {
            if (username.equals(book.getBorrowedBy())) {
                userBooks.add(book);
            }
        }
        return userBooks;
    }


    public boolean borrowBook(User user, Book book) {
        for (Book b : books) {
            if (Objects.equals(b.getTitle(), book.getTitle()) &&
                    Objects.equals(b.getAuthor(), book.getAuthor()) &&
                    !b.isBorrowed()) {
                b.borrowBook(user.getUsername());
                user.borrowBook(b.getTitle());
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(User user, Book book) {
        for (Book b : books) {
            if (Objects.equals(b.getTitle(), book.getTitle()) &&
                    b.isBorrowed() &&
                    Objects.equals(b.getBorrowedBy(), user.getUsername())) {
                b.returnBook();
                user.returnBook(b.getTitle());
                return true;
            }
        }
        return false;
    }

    public List<Book> getUserBooks(User user) {
        List<Book> userBooks = new ArrayList<>();
        for (Book book : books) {
            if (Objects.equals(book.getBorrowedBy(), user.getUsername())) {
                userBooks.add(book);
            }
        }
        return userBooks;
    }
}
