package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.entity.User;
import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.model.request.ShipperCreateRequest;
import com.nminh.quanlythuvien.model.response.ApiResponse;
import com.nminh.quanlythuvien.repository.BookOrderRepository;
import com.nminh.quanlythuvien.repository.ShipperRepository;
import com.nminh.quanlythuvien.repository.UserRepository;
import com.nminh.quanlythuvien.service.ShipperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipper")
public class ShipperController {

    @Autowired
    private ShipperService shipperService;

    @PostMapping("/create")
    public ApiResponse createShipper(@RequestBody ShipperCreateRequest shipperCreateRequest) {
        return new ApiResponse(shipperService.createShipper(shipperCreateRequest));
    }

    @GetMapping("/get-all")
    public ApiResponse getAllShippers() {
        return new ApiResponse(shipperService.getAllShipper());
    }

    @GetMapping("/get-book-order")
    public ApiResponse getAllBookOrder(@RequestParam String userId) {
        return new ApiResponse(shipperService.getAllBookOrder(userId));
    }

    @PutMapping("/start-shipping")
    public ApiResponse startShipping(@RequestParam String shippingId) {
        return new ApiResponse(shipperService.startShipping(shippingId));
    }
    @PutMapping("/delivered")
    public ApiResponse delivered(@RequestParam String shippingId) {
        return new ApiResponse(shipperService.delivered(shippingId));
    }
    @PutMapping("/failed")
    public ApiResponse failed(@RequestParam String shippingId) {
        return new ApiResponse(shipperService.failed(shippingId));
    }
}
