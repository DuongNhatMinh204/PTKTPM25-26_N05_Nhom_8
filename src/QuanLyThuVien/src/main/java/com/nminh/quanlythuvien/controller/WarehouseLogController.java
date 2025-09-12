package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.WarehouseLog;
import com.nminh.quanlythuvien.repository.WarehouseLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storekeeper/warehouse-logs")
public class WarehouseLogController {

    @Autowired
    private WarehouseLogRepository warehouseLogRepository;

    // Lấy toàn bộ log nhập/xuất
    @GetMapping
    public List<WarehouseLog> getAllLogs() {
        return warehouseLogRepository.findAll();
    }

    // (Tuỳ chọn) Lấy log theo mã sách
    @GetMapping("/book/{bookId}")
    public List<WarehouseLog> getLogsByBook(@PathVariable String bookId) {
        return warehouseLogRepository.findByBook_Id(bookId);
    }
}
