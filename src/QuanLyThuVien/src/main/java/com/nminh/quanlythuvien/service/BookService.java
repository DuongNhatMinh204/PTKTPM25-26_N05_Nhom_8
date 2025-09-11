package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.BookDTORequest;
import com.nminh.quanlythuvien.model.response.BookUpdateDtoResponse;

public interface BookService {
    String addBook(BookDTORequest bookDTORequest);
    String deleteBook(String bookId);
    BookUpdateDtoResponse updateBook(String id, BookDTORequest bookDTORequest);
}
