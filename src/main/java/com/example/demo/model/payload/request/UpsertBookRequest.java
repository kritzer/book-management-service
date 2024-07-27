package com.example.demo.model.payload.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpsertBookRequest {

    private String title;
    private String author;
    private String isbn;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate publishedDate;

    public UpsertBookRequest() {
    }

    public UpsertBookRequest(String title, String author, String isbn, LocalDate publishedDate) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpsertBookRequest that = (UpsertBookRequest) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(author, that.author) &&
                Objects.equals(isbn, that.isbn) &&
                Objects.equals(publishedDate, that.publishedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, isbn, publishedDate);
    }

    @Override
    public String toString() {
        return "UpdateBookRequest{" +
                " title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publishedDate=" + publishedDate +
                '}';
    }


    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (title == null || title.trim().isEmpty()) {
            errors.add("Title cannot be empty");
        } else if (title.length() > 255) {
            errors.add("Title must not exceed 255 characters");
        }

        if (author == null || author.trim().isEmpty()) {
            errors.add("Author cannot be empty");
        } else if (author.length() > 255) {
            errors.add("Author must not exceed 255 characters");
        }

        if (publishedDate == null) {
            errors.add("Published date cannot be null");
        }

        return errors;
    }
}