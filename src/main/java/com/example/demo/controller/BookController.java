package com.example.demo.controller;

import com.example.demo.exception.CentralizedError;
import com.example.demo.exception.CentralizedException;
import com.example.demo.model.entity.Book;
import com.example.demo.model.payload.request.UpsertBookRequest;
import com.example.demo.model.payload.response.CentralizedResponse;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.demo.types.ErrorResponseEnum.BOOK_ID_NOT_FOUND;
import static com.example.demo.types.ErrorResponseEnum.UPDATE_BOOK_BAD_REQUEST;
import static com.example.demo.util.Protocol.BOOKS_API;
import static com.example.demo.util.Protocol.ID_PATH_VARIABLE;

import java.util.Optional;

@RestController
@RequestMapping(BOOKS_API)
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<CentralizedResponse<Book>> getAllBooks() {
        List<Book> bookList = bookService.getAllBooks();
        return ResponseEntity.ok(new CentralizedResponse<>(bookList));
    }

    @GetMapping(ID_PATH_VARIABLE)
    public ResponseEntity<CentralizedResponse<Book>> getBookById(@PathVariable Long id) throws CentralizedException {
        Optional<Book> bookById = bookService.getBookById(id);
        if (bookById.isEmpty()) {
            throw new CentralizedException(new CentralizedError(BOOK_ID_NOT_FOUND, id), HttpStatus.NOT_FOUND);
        }
        var response = new CentralizedResponse<>(bookById.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CentralizedResponse<Book>> addBook(@RequestBody UpsertBookRequest request) throws CentralizedException {
        bookService.validateBookRequest(request);
        Book book = bookService.addBook(request);
        return ResponseEntity.ok(new CentralizedResponse<>(book));
    }

    @PutMapping(ID_PATH_VARIABLE)
    public ResponseEntity<CentralizedResponse<Book>> updateBook(@PathVariable Long id,
                                                                @RequestBody UpsertBookRequest request) throws CentralizedException {
        bookService.validateBookRequest(request);
        try {
            Book updatedBook = bookService.updateBook(id, request);
            return ResponseEntity.ok(new CentralizedResponse<>(updatedBook));
        } catch (RuntimeException e) {
            throw new CentralizedException(new CentralizedError(BOOK_ID_NOT_FOUND, id), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(ID_PATH_VARIABLE)
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) throws CentralizedException {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            throw new CentralizedException(new CentralizedError(BOOK_ID_NOT_FOUND, id), HttpStatus.NOT_FOUND);
        }
    }
}