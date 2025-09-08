package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.Book;
import com.nminh.quanlythuvien.model.request.BookDTORequest;
import com.nminh.quanlythuvien.repository.BookRepository;
import com.nminh.quanlythuvien.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public String addBook(BookDTORequest bookDTORequest) {
        Book book = Book.builder()
                .bookName(bookDTORequest.getBookName())
                .bookPublisher(bookDTORequest.getBookPublisher())
                .bookGerne(bookDTORequest.getBookGerne())
                .authorship(bookDTORequest.getAuthorship())
                .price(bookDTORequest.getPrice())
                .quantity(bookDTORequest.getQuantity())
                .imageUrl(bookDTORequest.getImageUrl())
                .build();
        bookRepository.save(book);
        return "Add book successful";
    }
}
