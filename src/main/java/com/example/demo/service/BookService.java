package com.example.demo.service;

import com.example.demo.exception.CentralizedError;
import com.example.demo.exception.CentralizedException;
import com.example.demo.model.entity.Book;
import com.example.demo.model.payload.request.UpsertBookRequest;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.demo.types.ErrorResponseEnum.UPDATE_BOOK_BAD_REQUEST;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book addBook(UpsertBookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublishedDate(request.getPublishedDate());
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, UpsertBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublishedDate(request.getPublishedDate());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.deleteById(id);
    }

    public void validateBookRequest(UpsertBookRequest request) throws CentralizedException {
        List<String> errorStrList = request.validate();
        if (!errorStrList.isEmpty()) {
            var errorResponse = new CentralizedError(UPDATE_BOOK_BAD_REQUEST.getCode(), errorStrList.toString());
            throw new CentralizedException(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

}
