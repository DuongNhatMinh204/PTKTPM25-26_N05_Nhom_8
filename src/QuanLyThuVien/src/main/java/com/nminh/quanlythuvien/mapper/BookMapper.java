package com.nminh.quanlythuvien.mapper;

import com.nminh.quanlythuvien.entity.Book;
import com.nminh.quanlythuvien.model.response.BookDtoResponse;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public BookDtoResponse toBookDto(Book book) {
        BookDtoResponse bookDtoResponse = new BookDtoResponse();

        bookDtoResponse.setId(book.getId());
        bookDtoResponse.setBookName(book.getBookName());
        bookDtoResponse.setAuthorship(book.getAuthorship());
        bookDtoResponse.setBookGerne(book.getBookGerne());
        bookDtoResponse.setBookPublisher(book.getBookPublisher());
        bookDtoResponse.setQuantity(book.getQuantity());
        bookDtoResponse.setPrice(book.getPrice());
        bookDtoResponse.setImageUrl(book.getImageUrl());
        bookDtoResponse.setStatus(book.getStatus());

        return bookDtoResponse;
    }
}
