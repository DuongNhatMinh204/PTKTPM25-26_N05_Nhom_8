package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.service.ShipperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/shipper")
public class ShipperController {

    @Autowired
    private ShipperService shipperService;

    // ✅ API lấy thông tin shipper theo ID (hoặc bạn có thể dùng SĐT, hoặc thông tin từ Principal khi có login)
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@RequestParam String phone) {
        Optional<Shipper> shipper = shipperService.getShipperByPhone(phone);
        if (shipper.isPresent()) {
            return ResponseEntity.ok(shipper.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ (Tuỳ chọn) API này sử dụng thông tin từ Spring Security - nếu bạn có cấu hình đăng nhập rồi
    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        String phone = principal.getName(); // giả sử bạn dùng SĐT làm username khi login
        Optional<Shipper> shipper = shipperService.getShipperByPhone(phone);
        return shipper.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
