package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.repository.BookOrderRepository;
import com.nminh.quanlythuvien.repository.ShipperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipper")
public class ShipperController {

    @Autowired
    private ShipperRepository shipperRepository;

    @Autowired
    private BookOrderRepository bookOrderRepository;

    // 1. Xem hồ sơ của shipper
    @GetMapping("/profile/{id}")
    public ResponseEntity<Shipper> getShipperProfile(@PathVariable String id) {
        return shipperRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2. Xem tất cả đơn hàng của shipper đang ở trạng thái SHIPPING
    @GetMapping("/{shipperId}/orders")
    public ResponseEntity<List<BookOrder>> getShipperOrders(@PathVariable String shipperId) {
        List<BookOrder> orders = bookOrderRepository
                .findByShipping_Shipper_IdAndOrderStatus(shipperId, OrderStatus.SHIPPING);
        return ResponseEntity.ok(orders);
    }
}
