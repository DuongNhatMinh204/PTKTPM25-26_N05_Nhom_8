package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.model.request.BookOrderTempCreateRequestDTO;
import com.nminh.quanlythuvien.model.response.ApiResponse;
import com.nminh.quanlythuvien.service.BookOrderTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order-temp")
public class BookOrderTempController {
    @Autowired
    private BookOrderTempService bookOrderTempService;

    @PostMapping("/create")
    public ApiResponse addBookOrderTemp(@RequestBody BookOrderTempCreateRequestDTO bookOrderTempCreateDTO) {
        log.info("bookOrderTempCreateDTO: {}", bookOrderTempCreateDTO);
        ApiResponse apiResponse = new ApiResponse(bookOrderTempService.createBookOrderTemp(bookOrderTempCreateDTO));
        return apiResponse;
    }

    @GetMapping("/get-by-user/{userId}")
    public ApiResponse getBookOrderTempByUser(@PathVariable String userId) {
        log.info("getBookOrderTempByUser");
        ApiResponse apiResponse = new ApiResponse(bookOrderTempService.getAllBookOrderTempDetail(userId));
        return apiResponse;
    }
}
