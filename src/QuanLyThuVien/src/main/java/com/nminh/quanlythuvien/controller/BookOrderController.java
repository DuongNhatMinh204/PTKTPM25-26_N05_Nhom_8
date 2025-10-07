package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.model.request.BookOrderCreateRequest;
import com.nminh.quanlythuvien.model.response.ApiResponse;
import com.nminh.quanlythuvien.model.response.BookOrderResponse;
import com.nminh.quanlythuvien.service.BookOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class BookOrderController {
    @Autowired
    private BookOrderService bookOrderService;

    // Lấy danh sách đơn hàng với status la PENDING
    @GetMapping("/get-all-order")
    public ApiResponse<List<BookOrderResponse>> getAllOrderPending() {
        log.info("get-all-order pending");
        ApiResponse apiResponse = new ApiResponse(bookOrderService.orderPendingList());
        return apiResponse;
    }

    // Tạo đơn hàng mới dành cho user
    @PostMapping("/create")
    public ApiResponse createBookOrder(@RequestBody BookOrderCreateRequest bookOrderCreateRequest){
        log.info("create book order");
        return new ApiResponse(bookOrderService.createBookOrder(bookOrderCreateRequest));
    }

    // Xác nhận đơn hàng dành cho admin
    @PutMapping("/admin-confirm/{id}")
    public ApiResponse confirmBookOrder(@PathVariable String id){
        log.info("Admin confirm book order : {} ",id);
        ApiResponse apiResponse = new ApiResponse(bookOrderService.confirmBookOrderToAproved(id));
        return apiResponse;
    }

    @GetMapping("/detail/{id}")
    public ApiResponse detailBookOrder(@PathVariable String id){
        log.info("detail book order : {} ",id);
        ApiResponse apiResponse = new ApiResponse(bookOrderService.orderDetailList(id));
        return apiResponse;
    }

    @GetMapping("/get-all")
    public ApiResponse getAllBookOrder(@RequestParam String userId){
        return new ApiResponse(bookOrderService.getAll(userId));
    }
}
