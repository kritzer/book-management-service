package com.example.demo.controller;

import com.example.demo.exception.CentralizedError;
import com.example.demo.exception.CentralizedException;
import com.example.demo.model.entity.Book;
import com.example.demo.model.payload.response.CentralizedResponse;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.demo.types.ErrorResponseEnum.BOOK_ID_NOT_FOUND;
import static com.example.demo.util.Protocol.API_VERSION_1;

import java.util.Optional;

@RestController
@RequestMapping(API_VERSION_1)
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<CentralizedResponse<Book>> getAllBooks() {
        List<Book> bookList = bookService.getAllBooks();
        return ResponseEntity.ok(new CentralizedResponse<>(bookList));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<CentralizedResponse<Book>> getBookById(@PathVariable Long id) throws CentralizedException {
        Optional<Book> bookById = bookService.getBookById(id);
        if (bookById.isEmpty()) {
            throw new CentralizedException(new CentralizedError(BOOK_ID_NOT_FOUND, id), HttpStatus.NOT_FOUND);
        }
        var response = new CentralizedResponse<>(bookById.get());
        return ResponseEntity.ok(response);
    }

//    @PostMapping
//    public Book addBook(@RequestBody Book book) {
//        return bookService.addBook(book);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
//        try {
//            Book updatedBook = bookService.updateBook(id, bookDetails);
//            return ResponseEntity.ok(updatedBook);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
//        bookService.deleteBook(id);
//        return ResponseEntity.ok().build();
//    }
}