package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.service.BookOrderService;
import com.nminh.quanlythuvien.service.ShipperService;
import com.nminh.quanlythuvien.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shipper")
public class ShipperController {

    @Autowired
    private ShipperService shipperService;

    @Autowired
    private BookOrderService bookOrderService;

    @Autowired
    private ShippingService shippingService;

    // ✅ API lấy thông tin shipper theo SĐT (test)
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@RequestParam String phone) {
        Optional<Shipper> shipper = shipperService.getShipperByPhone(phone);
        if (shipper.isPresent()) {
            return ResponseEntity.ok(shipper.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ API lấy profile shipper theo người đăng nhập (nếu login bằng Spring Security)
    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        String phone = principal.getName(); // giả sử dùng số điện thoại làm username
        Optional<Shipper> shipper = shipperService.getShipperByPhone(phone);
        return shipper.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Xem danh sách các đơn hàng SHIPPING chưa có người nhận
    @GetMapping("/orders/shipping")
    public ResponseEntity<?> getShippingOrders() {
        List<BookOrder> orders = bookOrderService.getOrdersWithShippingStatusAndNoShipper();
        return ResponseEntity.ok(orders);
    }

    // ✅ Nhận đơn hàng (gán shipper đang đăng nhập vào đơn)
    @PutMapping("/orders/{orderId}/accept")
    public ResponseEntity<?> acceptOrder(@PathVariable String orderId, @RequestParam String phone) {
        Optional<Shipper> optionalShipper = shipperService.getShipperByPhone(phone);

        if (optionalShipper.isEmpty()) {
            return ResponseEntity.status(404).body("Shipper không tồn tại");
        }

        boolean success = shippingService.assignShippingToShipper(orderId, optionalShipper.get());

        if (success) {
            return ResponseEntity.ok("Nhận đơn hàng thành công");
        } else {
            return ResponseEntity.badRequest().body("Không thể nhận đơn (đơn đã được nhận hoặc không tồn tại)");
        }
    }
    // Lấy danh sách đơn shipper đã nhận
    @GetMapping("/orders/my")
    public ResponseEntity<?> getMyOrders(@RequestParam String phone) {
        Optional<Shipper> optionalShipper = shipperService.getShipperByPhone(phone);
        if (optionalShipper.isEmpty()) {
            return ResponseEntity.status(404).body("Shipper không tồn tại");
        }
        Shipper shipper = optionalShipper.get();
        List<BookOrder> orders = bookOrderService.getOrdersByShipper(shipper.getId());
        return ResponseEntity.ok(orders);
    }

}
