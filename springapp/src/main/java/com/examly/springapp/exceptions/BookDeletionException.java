package com.examly.springapp.exceptions;

public class BookDeletionException extends RuntimeException {
    public BookDeletionException(String message) {
        super(message);
    }
}
