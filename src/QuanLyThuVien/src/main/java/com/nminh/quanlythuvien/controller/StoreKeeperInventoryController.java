package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.Book;
import com.nminh.quanlythuvien.service.impl.WarehouseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/storekeeper/books")
public class StoreKeeperInventoryController {

    @Autowired
    private WarehouseServiceImpl bookInventoryServiceImpl;

    // GET: Lấy danh sách toàn bộ sách trong kho
    @GetMapping
    public List<Book> getAllBooks() {
        return bookInventoryServiceImpl.getAllBooks();
    }

    // PUT: Tăng số lượng sách
    @PutMapping("/{id}/add")
    public ResponseEntity<String> increaseQuantity(@PathVariable String id, @RequestBody Map<String, Integer> request) {
        int qty = request.getOrDefault("quantity", 0);
        if (qty <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        bookInventoryServiceImpl.increaseQuantity(id, qty);
        return ResponseEntity.ok("Cập nhật số lượng sách thành công: +" + qty);
    }

    // PUT: Giảm số lượng sách
    @PutMapping("/{id}/subtract")
    public ResponseEntity<String> decreaseQuantity(@PathVariable String id, @RequestBody Map<String, Integer> request) {
        int qty = request.getOrDefault("quantity", 0);
        if (qty <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        bookInventoryServiceImpl.decreaseQuantity(id, qty);
        return ResponseEntity.ok("Cập nhật số lượng sách thành công: -" + qty);
    }
}
