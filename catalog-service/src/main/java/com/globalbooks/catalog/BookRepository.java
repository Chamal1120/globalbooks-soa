package com.globalbooks.catalog;

import com.globalbooks.catalog.generated.Book;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class BookRepository {

    private static final Map<String, Book> books = new HashMap<>();

    @PostConstruct
    public void init() {
        Book book1 = new Book();
        book1.setId("1");
        book1.setTitle("The Great Gatsby");
        book1.setAuthor("F. Scott Fitzgerald");
        books.put(book1.getId(), book1);

        Book book2 = new Book();
        book2.setId("2");
        book2.setTitle("To Kill a Mockingbird");
        book2.setAuthor("Harper Lee");
        books.put(book2.getId(), book2);

        Book book3 = new Book();
        book3.setId("3");
        book3.setTitle("1984");
        book3.setAuthor("George Orwell");
        books.put(book3.getId(), book3);
    }

    public Book findBookById(String id) {
        return books.get(id);
    }
}
