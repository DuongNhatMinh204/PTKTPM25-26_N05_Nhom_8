package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.BookDTORequest;
import com.nminh.quanlythuvien.model.response.BookDtoResponse;
import com.nminh.quanlythuvien.model.response.BookUpdateDtoResponse;

import java.util.List;

public interface BookService {
    String addBook(BookDTORequest bookDTORequest);
    String deleteBook(String bookId);
    BookUpdateDtoResponse updateBook(String id, BookDTORequest bookDTORequest);
    List<BookDtoResponse> getAllBooksActive();
}
