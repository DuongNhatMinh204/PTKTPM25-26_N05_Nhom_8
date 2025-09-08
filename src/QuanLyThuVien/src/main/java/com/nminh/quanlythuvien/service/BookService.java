package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.BookDTORequest;

public interface BookService {
    String addBook(BookDTORequest bookDTORequest);
}
