package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.constant.Constants;
import com.nminh.quanlythuvien.constant.MessageConstant;
import com.nminh.quanlythuvien.entity.Book;
import com.nminh.quanlythuvien.entity.WarehouseLog;
import com.nminh.quanlythuvien.enums.ActionType;
import com.nminh.quanlythuvien.model.request.BookDTORequest;
import com.nminh.quanlythuvien.repository.BookRepository;
import com.nminh.quanlythuvien.repository.WarehouseLogRepository;
import com.nminh.quanlythuvien.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WarehouseLogRepository warehouseLogRepository;
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
                .status(Constants.INACTIVE_STATUS)
                .build();
        bookRepository.save(book);
        WarehouseLog warehouseLog = WarehouseLog.builder()
                .book(book)
                .note(MessageConstant.MESSAGE_ADD_NEW_BOOK)
                .currentQuantity(bookDTORequest.getQuantity())
                .preQuantity(0)
                .actionType(ActionType.IMPORT)
                .build();
        warehouseLogRepository.save(warehouseLog);
        return MessageConstant.MESSAGE_ADD_NEW_BOOK;
    }
}
