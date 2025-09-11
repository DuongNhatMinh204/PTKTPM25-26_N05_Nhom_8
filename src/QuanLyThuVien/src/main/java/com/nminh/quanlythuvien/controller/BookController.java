package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.model.request.BookDTORequest;
import com.nminh.quanlythuvien.model.response.ApiResponse;
import com.nminh.quanlythuvien.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public ApiResponse addBook(@RequestBody BookDTORequest bookDTORequest) {
        log.info("bookDTORequest: {}", bookDTORequest);
        ApiResponse apiResponse = new ApiResponse(bookService.addBook(bookDTORequest));
        return  apiResponse;
    }

}
