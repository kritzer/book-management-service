package com.example.demo.controller;


import com.example.demo.exception.CentralizedException;
import com.example.demo.model.entity.Book;
import com.example.demo.model.payload.request.UpsertBookRequest;
import com.example.demo.model.payload.response.CentralizedResponse;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book testBook;
    private UpsertBookRequest testRequest;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setPublishedDate(LocalDate.now());

        testRequest = new UpsertBookRequest();
        testRequest.setTitle("New Book");
        testRequest.setAuthor("New Author");
        testRequest.setIsbn("0987654321");
        testRequest.setPublishedDate(LocalDate.now());
    }

    @Nested
    class GetAllBooksTests {
        @Test
        void shouldReturnListOfBooks() {
            List<Book> books = Collections.singletonList(testBook);
            when(bookService.getAllBooks()).thenReturn(books);

            ResponseEntity<CentralizedResponse<Book>> response = bookController.getAllBooks();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getData());
            assertEquals(1, response.getBody().getData().size());
            assertEquals(testBook, response.getBody().getData().get(0));
        }
    }

    @Nested
    class GetBookByIdTests {
        @Test
        void whenBookExists_shouldReturnBook() throws CentralizedException {
            when(bookService.getBookById(1L)).thenReturn(Optional.of(testBook));

            ResponseEntity<CentralizedResponse<Book>> response = bookController.getBookById(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getData());
            assertEquals(testBook, response.getBody().getData().get(0));
        }

        @Test
        void whenBookDoesNotExist_shouldThrowException() {
            when(bookService.getBookById(1L)).thenReturn(Optional.empty());

            assertThrows(CentralizedException.class, () -> bookController.getBookById(1L));
        }
    }

    @Nested
    class AddBookTests {
        @Test
        void shouldReturnNewBook() throws CentralizedException {
            when(bookService.addBook(testRequest)).thenReturn(testBook);

            ResponseEntity<CentralizedResponse<Book>> response = bookController.addBook(testRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testBook, response.getBody().getData().get(0));
            verify(bookService).validateBookRequest(testRequest);
        }
    }

    @Nested
    class UpdateBookTests {
        @Test
        void whenBookExists_shouldReturnUpdatedBook() throws CentralizedException {
            when(bookService.updateBook(1L, testRequest)).thenReturn(testBook);

            ResponseEntity<CentralizedResponse<Book>> response = bookController.updateBook(1L, testRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testBook, response.getBody().getData().get(0));
            verify(bookService).validateBookRequest(testRequest);
        }

        @Test
        void whenBookDoesNotExist_shouldThrowException() throws CentralizedException {
            when(bookService.updateBook(1L, testRequest)).thenThrow(new RuntimeException("Book not found"));

            assertThrows(CentralizedException.class, () -> bookController.updateBook(1L, testRequest));
            verify(bookService).validateBookRequest(testRequest);
        }
    }

    @Nested
    class DeleteBookTests {
        @Test
        void whenBookExists_shouldReturnOkResponse() throws CentralizedException {
            doNothing().when(bookService).deleteBook(1L);

            ResponseEntity<Void> response = bookController.deleteBook(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void whenBookDoesNotExist_shouldThrowException() {
            doThrow(new RuntimeException("Book not found")).when(bookService).deleteBook(1L);

            assertThrows(CentralizedException.class, () -> bookController.deleteBook(1L));
        }
    }
}