package com.examly.springapp.mapper;

import com.examly.springapp.dto.BookRequestDTO;
import com.examly.springapp.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookRequestDTO dto) {
        Book book = new Book();
        updateEntityFromDto(dto, book);
        return book;
    }

    public void updateEntityFromDto(BookRequestDTO dto, Book book) {
        if (dto.title() != null) book.setTitle(dto.title());
        if (dto.author() != null) book.setAuthor(dto.author());
        if (dto.genre() != null) book.setGenre(dto.genre());
        if (dto.description() != null) book.setDescription(dto.description());
        if (dto.rentalFee() != null) book.setRentalFee(dto.rentalFee());
        if (dto.isAvailable() != null) book.setIsAvailable(dto.isAvailable());
        if (dto.coverImage() != null) book.setCoverImage(dto.coverImage());
    }
}
