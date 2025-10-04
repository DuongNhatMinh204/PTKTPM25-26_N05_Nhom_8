package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import com.nminh.quanlythuvien.repository.BookOrderRepository;
import com.nminh.quanlythuvien.repository.ShipperRepository;
import com.nminh.quanlythuvien.repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipper/shipping")
public class ShippingController {

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private BookOrderRepository bookOrderRepository;

    @Autowired
    private ShipperRepository shipperRepository;

    // Lấy danh sách đơn hàng của shipper dựa trên phone
    @GetMapping("/shipper/{phone}")
    public ResponseEntity<List<BookOrder>> getOrdersByShipper(@PathVariable String phone) {
        Shipper shipper = shipperRepository.findByShipperPhone(phone)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper"));
        List<BookOrder> orders = bookOrderRepository.findOrdersByShipperId(shipper.getId());
        return ResponseEntity.ok(orders);
    }

    // Lấy danh sách đơn hàng chờ nhận (SHIPPING, chưa có shipper)
    @GetMapping("/orders/shipping")
    public ResponseEntity<List<BookOrder>> getShippingOrders() {
        List<BookOrder> orders = bookOrderRepository.findByOrderStatusAndShipping_ShipperIsNull(OrderStatus.SHIPPING);
        return ResponseEntity.ok(orders);
    }

    // Nhận đơn → SHIPPING
    @PutMapping("/{id}/accept")
    public ResponseEntity<String> acceptOrder(@PathVariable String id, @RequestParam String phone) {
        BookOrder order = bookOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra trạng thái đơn hàng
        if (order.getOrderStatus() != OrderStatus.SHIPPING) {
            throw new RuntimeException("Đơn hàng không ở trạng thái SHIPPING");
        }

        Shipping shipping = order.getShipping();
        // Nếu chưa có Shipping, tạo mới
        if (shipping == null) {
            shipping = new Shipping();
            shipping.setBookOrder(order);
            shipping.setShippingStatus(ShippingStatus.PENDING);
            order.setShipping(shipping);
        }

        // Kiểm tra trạng thái hiện tại của shipping
        if (shipping.getShippingStatus() != ShippingStatus.PENDING) {
            throw new RuntimeException("Đơn không ở trạng thái PENDING");
        }

        // Gán shipper dựa trên phone
        Shipper shipper = shipperRepository.findByShipperPhone(phone)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper"));
        shipping.setShipper(shipper);

        // Cập nhật trạng thái sang SHIPPING
        shipping.setShippingStatus(ShippingStatus.SHIPPING);
        shipping.setShippingDate(new Date());
        shippingRepository.save(shipping);
        bookOrderRepository.save(order); // Lưu order để đảm bảo liên kết

        return ResponseEntity.ok("Shipper đã nhận đơn - chuyển sang SHIPPING");
    }

    // Giao thành công → DELIVERED, cập nhật OrderStatus → COMPLETED
    @PutMapping("/{id}/delivered")
    public ResponseEntity<String> delivered(@PathVariable String id) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giao hàng"));

        BookOrder order = bookOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (shipping.getShippingStatus() != ShippingStatus.SHIPPING) {
            throw new RuntimeException("Đơn không ở trạng thái SHIPPING");
        }

        shipping.setShippingStatus(ShippingStatus.DELIVERED);
        shipping.setDeliveredDate(new Date());
        shippingRepository.save(shipping);

        order.setOrderStatus(OrderStatus.COMPLETED);
        bookOrderRepository.save(order);

        return ResponseEntity.ok("Đơn hàng đã được giao thành công");
    }

    // Giao thất bại → FAILED, cập nhật OrderStatus → CANCELLED
    @PutMapping("/{id}/failed")
    public ResponseEntity<String> failed(@PathVariable String id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do thất bại");
        }

        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giao hàng"));

        BookOrder order = bookOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (shipping.getShippingStatus() != ShippingStatus.SHIPPING) {
            throw new RuntimeException("Chỉ có thể hủy đơn đang giao");
        }

        shipping.setShippingStatus(ShippingStatus.FAILED);
        shipping.setDeliveredDate(new Date());
        shipping.setNote(reason);
        shippingRepository.save(shipping);

        order.setOrderStatus(OrderStatus.CANCELLED);
        bookOrderRepository.save(order);

        return ResponseEntity.ok("Đã đánh dấu đơn giao hàng là FAILED");
    }
}