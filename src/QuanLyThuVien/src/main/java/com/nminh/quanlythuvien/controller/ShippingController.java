package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import com.nminh.quanlythuvien.repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/shipper/shipping")
public class ShippingController {

    @Autowired
    private ShippingRepository shippingRepository;

    // 3. Nhận đơn → PENDING
    @PutMapping("/{id}/accept")
    public ResponseEntity<String> acceptOrder(@PathVariable String id) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giao hàng"));

        shipping.setShippingStatus(ShippingStatus.PENDING);
        shippingRepository.save(shipping);
        return ResponseEntity.ok("Shipper đã nhận đơn - chuyển sang PENDING");
    }

    // 4. Bắt đầu giao → SHIPPING
    @PutMapping("/{id}/start")
    public ResponseEntity<String> startShipping(@PathVariable String id) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giao hàng"));

        if (shipping.getShippingStatus() != ShippingStatus.PENDING) {
            throw new RuntimeException("Đơn chưa ở trạng thái PENDING");
        }

        shipping.setShippingStatus(ShippingStatus.SHIPPING);
        shipping.setShippingDate(new Date());
        shippingRepository.save(shipping);
        return ResponseEntity.ok("Đã bắt đầu giao hàng");
    }

    // 5. Giao thành công → DELIVERED
    @PutMapping("/{id}/delivered")
    public ResponseEntity<String> delivered(@PathVariable String id) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giao hàng"));

        if (shipping.getShippingStatus() != ShippingStatus.SHIPPING) {
            throw new RuntimeException("Đơn không ở trạng thái SHIPPING");
        }

        shipping.setShippingStatus(ShippingStatus.DELIVERED);
        shipping.setDeliveredDate(new Date());
        shippingRepository.save(shipping);
        return ResponseEntity.ok("Đơn hàng đã được giao thành công");
    }

    // 6. Giao thất bại → FAILED
    @PutMapping("/{id}/failed")
    public ResponseEntity<String> failed(@PathVariable String id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do thất bại");
        }

        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giao hàng"));

        if (shipping.getShippingStatus() != ShippingStatus.SHIPPING) {
            throw new RuntimeException("Chỉ có thể hủy đơn đang giao");
        }

        shipping.setShippingStatus(ShippingStatus.FAILED);
        shipping.setDeliveredDate(new Date());
        shipping.setNote(reason);
        shippingRepository.save(shipping);
        return ResponseEntity.ok("Đã đánh dấu đơn giao hàng là FAILED");
    }
}
