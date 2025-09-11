package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.model.request.BookDTORequest;
import com.nminh.quanlythuvien.model.response.ApiResponse;
import com.nminh.quanlythuvien.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteBook(@PathVariable String id) {
        log.info("Delete bookId: {}", id);
        ApiResponse apiResponse = new ApiResponse(bookService.deleteBook(id));
        return  apiResponse;
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateBook(@PathVariable String id, @RequestBody BookDTORequest bookDTORequest) {
        log.info("Update bookId: {}", id);
        ApiResponse apiResponse = new ApiResponse(bookService.updateBook(id, bookDTORequest));
        return  apiResponse;
    }

}
