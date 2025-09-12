package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.BookDTORequest;
import com.nminh.quanlythuvien.model.response.BookDtoResponse;
import com.nminh.quanlythuvien.model.response.BookUpdateDtoResponse;

import java.util.List;
import java.util.Objects;

public interface BookService {
    String addBook(BookDTORequest bookDTORequest);
    String deleteBook(String bookId);
    BookUpdateDtoResponse updateBook(String id, BookDTORequest bookDTORequest);
    Object getAllBooksActive(Integer page, Integer size);
    Object searchBook(Integer page, Integer size,String keyword);
}
