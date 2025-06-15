package Library.ui;

import Library.model.Book;
import javax.swing.*;
import java.awt.*;

public class BookListRenderer extends DefaultListCellRenderer {
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