package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.service.impl.BookOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/librarian/orders")
public class LibrarianOrderController {

    @Autowired
    private BookOrderServiceImpl bookOrderService;

    // Lấy danh sách tất cả đơn hàng có status APPROVED
    @GetMapping("/approved")
    public List<BookOrder> getApprovedOrders() {
        return bookOrderService.getApprovedOrders();
    }

    // Xác nhận đơn hàng (chuyển sang SHIPPING)
    @PutMapping("/confirm")
    public ResponseEntity<String> confirmOrder(@RequestParam String bookOrderId,@RequestParam String shipperId) {
        bookOrderService.confirmOrder(bookOrderId,shipperId);
        return ResponseEntity.ok("Xác nhận đơn hàng thành công");
    }


    // Hủy đơn hàng (chuyển sang CANCELLED, yêu cầu lý do)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable String id, @RequestBody Map<String, String> request) {
        String cancelReason = request.get("cancelReason");
        if (cancelReason == null || cancelReason.isEmpty()) {
            throw new RuntimeException("Cancel reason is required");
        }
        bookOrderService.cancelOrder(id, cancelReason);
        return ResponseEntity.ok("Đơn hàng đã được hủy");
    }
}