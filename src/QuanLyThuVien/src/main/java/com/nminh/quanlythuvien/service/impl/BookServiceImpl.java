package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.constant.Constants;
import com.nminh.quanlythuvien.constant.MessageConstant;
import com.nminh.quanlythuvien.entity.Book;
import com.nminh.quanlythuvien.entity.WarehouseLog;
import com.nminh.quanlythuvien.enums.ActionType;
import com.nminh.quanlythuvien.enums.ErrorCode;
import com.nminh.quanlythuvien.exception.AppException;
import com.nminh.quanlythuvien.mapper.BookMapper;
import com.nminh.quanlythuvien.model.request.BookDTORequest;
import com.nminh.quanlythuvien.model.response.BookDtoResponse;
import com.nminh.quanlythuvien.model.response.BookUpdateDtoResponse;
import com.nminh.quanlythuvien.repository.BookRepository;
import com.nminh.quanlythuvien.repository.WarehouseLogRepository;
import com.nminh.quanlythuvien.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WarehouseLogRepository warehouseLogRepository;

    @Autowired
    private BookMapper bookMapper;

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
                .status(Constants.ACTIVE_STATUS)
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

    @Override
    public String deleteBook(String bookId) {
        Book bookDeleted = bookRepository.findById(bookId)
                .orElseThrow(()-> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        bookDeleted.setStatus(Constants.INACTIVE_STATUS);
        bookRepository.save(bookDeleted);
        return MessageConstant.MESSAGE_DELETE_BOOK;
    }

    @Override
    public BookUpdateDtoResponse updateBook(String id, BookDTORequest bookDTORequest) {
        Book bookUpdated = bookRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        bookUpdated.setBookName(bookDTORequest.getBookName());
        bookUpdated.setBookPublisher(bookDTORequest.getBookPublisher());
        bookUpdated.setBookGerne(bookDTORequest.getBookGerne());
        bookUpdated.setAuthorship(bookDTORequest.getAuthorship());
        bookUpdated.setPrice(bookDTORequest.getPrice());
        bookUpdated.setQuantity(bookDTORequest.getQuantity());
        bookUpdated.setImageUrl(bookDTORequest.getImageUrl());
        bookUpdated.setStatus(Constants.ACTIVE_STATUS);

        bookRepository.save(bookUpdated);
        return BookUpdateDtoResponse.builder()
                .bookName(bookUpdated.getBookName())
                .bookPublisher(bookUpdated.getBookPublisher())
                .bookGerne(bookUpdated.getBookGerne())
                .authorship(bookUpdated.getAuthorship())
                .price(bookUpdated.getPrice())
                .quantity(bookUpdated.getQuantity())
                .imageUrl(bookUpdated.getImageUrl())
                .build();
    }

    @Override
    public Object getAllBooksActive(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page-1, size);

        Page<Book> booksPage = bookRepository.findByStatus(Constants.ACTIVE_STATUS,pageable);

        List<BookDtoResponse> bookDtoResponseList = booksPage
                .getContent()
                .stream()
                .map(bookMapper::toBookDto)
                .toList();

        Map<String,Object> response = new HashMap<>();

        response.put("books", bookDtoResponseList);
        response.put("currentPage", booksPage.getNumber()+1);
        response.put("totalPages", booksPage.getTotalPages());
        response.put("totalElements", booksPage.getTotalElements());

        return response;
    }

    @Override
    public Object searchBook(Integer page, Integer size,String keyword) {
        PageRequest pageable = PageRequest.of(page-1, size);
        String keywordAdd = "%"+keyword+"%";
        Page<Book> listPageable = bookRepository.findByKeyword(keywordAdd,pageable);

        List<BookDtoResponse> bookDtoResponseList = listPageable
                .getContent()
                .stream()
                .map(bookMapper::toBookDto)
                .toList();

        Map<String,Object> response = new HashMap<>();
        response.put("books", bookDtoResponseList);
        response.put("currentPage", listPageable.getNumber()+1);
        response.put("totalPages", listPageable.getTotalPages());
        response.put("totalElements", listPageable.getTotalElements());

        return response;
    }


}
