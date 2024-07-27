package com.example.demo.service;

import com.example.demo.exception.CentralizedException;
import com.example.demo.model.entity.Book;
import com.example.demo.model.payload.request.UpsertBookRequest;
import com.example.demo.repository.BookRepository;
import com.example.demo.types.ErrorResponseEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private UpsertBookRequest validRequest;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setPublishedDate(LocalDate.now());

        validRequest = new UpsertBookRequest();
        validRequest.setTitle("New Book");
        validRequest.setAuthor("New Author");
        validRequest.setIsbn("0987654321");
        validRequest.setPublishedDate(LocalDate.now());
    }

    @Test
    void getAllBooks_shouldReturnAllBooks() {
        List<Book> books = Collections.singletonList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(books, result);
        verify(bookRepository).findAll();
    }

    @Nested
    @DisplayName("GetBookByBookID")
    public class getBookByIDTest {
        @Test
        void getBookById_whenBookExists_shouldReturnBook() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

            Optional<Book> result = bookService.getBookById(1L);

            assertTrue(result.isPresent());
            assertEquals(testBook, result.get());
            verify(bookRepository).findById(1L);
        }

        @Test
        void getBookById_whenBookDoesNotExist_shouldReturnEmpty() {
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            Optional<Book> result = bookService.getBookById(1L);

            assertTrue(result.isEmpty());
            verify(bookRepository).findById(1L);
        }
    }

    @Test
    @DisplayName("AddBookTest")
    void addBook_shouldSaveAndReturnNewBook() {
        Book newBook = new Book();
        newBook.setTitle(validRequest.getTitle());
        newBook.setAuthor(validRequest.getAuthor());
        newBook.setIsbn(validRequest.getIsbn());
        newBook.setPublishedDate(validRequest.getPublishedDate());

        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        Book result = bookService.addBook(validRequest);

        assertEquals(newBook, result);
        verify(bookRepository).save(any(Book.class));
    }

    @Nested
    @DisplayName("UpdateBookTest")
    public class updateBookTest {
        @Test
        void updateBook_whenBookExists_shouldUpdateAndReturnBook() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
            when(bookRepository.save(any(Book.class))).thenReturn(testBook);

            Book result = bookService.updateBook(1L, validRequest);

            assertEquals(validRequest.getTitle(), result.getTitle());
            assertEquals(validRequest.getAuthor(), result.getAuthor());
            assertEquals(validRequest.getIsbn(), result.getIsbn());
            assertEquals(validRequest.getPublishedDate(), result.getPublishedDate());
            verify(bookRepository).findById(1L);
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        void updateBook_whenBookDoesNotExist_shouldThrowException() {
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> bookService.updateBook(1L, validRequest));
            verify(bookRepository).findById(1L);
            verify(bookRepository, never()).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("DeleteBookTest")
    public class deleteBookTest {
        @Test
        void deleteBook_whenBookExists_shouldDeleteBook() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

            bookService.deleteBook(1L);

            verify(bookRepository).findById(1L);
            verify(bookRepository).deleteById(1L);
        }

        @Test
        void deleteBook_whenBookDoesNotExist_shouldThrowException() {
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> bookService.deleteBook(1L));
            verify(bookRepository).findById(1L);
            verify(bookRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("ValidateUpsertBookTest")
    public class validateUpsertBookRequestTest {
        @Test
        void validateBookRequest_withValidRequest_shouldNotThrowException() {
            UpsertBookRequest validRequest = new UpsertBookRequest();
            validRequest.setTitle("Valid Title");
            validRequest.setAuthor("Valid Author");
            validRequest.setIsbn("1234567890");
            validRequest.setPublishedDate(LocalDate.now());

            assertDoesNotThrow(() -> bookService.validateBookRequest(validRequest));
        }

        @Test
        void validateBookRequest_withInvalidRequest_shouldThrowCentralizedException() {
            UpsertBookRequest invalidRequest = new UpsertBookRequest();

            CentralizedException exception = assertThrows(CentralizedException.class,
                    () -> bookService.validateBookRequest(invalidRequest));

            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
            assertEquals(ErrorResponseEnum.UPDATE_BOOK_BAD_REQUEST.getCode(), exception.getCode());
        }
    }

}