package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import com.nminh.quanlythuvien.repository.BookOrderRepository;
import com.nminh.quanlythuvien.repository.ShippingRepository;
import com.nminh.quanlythuvien.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import com.nminh.quanlythuvien.enums.OrderStatus;

import java.util.Optional;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private BookOrderRepository bookOrderRepository;

    @Autowired
    private ShippingRepository shippingRepository;

    @Override
    public boolean assignShippingToShipper(String orderId, Shipper shipper) {
        Optional<BookOrder> optionalOrder = bookOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return false;
        }

        BookOrder order = optionalOrder.get();
        Shipping shipping = order.getShipping();

        // Nếu chưa có Shipping thì tạo mới
        if (shipping == null) {
            shipping = new Shipping();
            shipping.setBookOrder(order);
            shipping.setShippingStatus(ShippingStatus.PENDING); // Khởi tạo ở trạng thái PENDING
            order.setShipping(shipping);
        }

        // Nếu đơn đã có shipper thì không nhận
        if (shipping.getShipper() != null) {
            return false;
        }

        // Kiểm tra trạng thái PENDING trước khi chuyển sang SHIPPING
        if (shipping.getShippingStatus() != ShippingStatus.PENDING) {
            return false; // Đơn không ở trạng thái PENDING, không thể nhận
        }

        // Gán shipper và chuyển trạng thái sang SHIPPING
        shipping.setShipper(shipper);
        shipping.setShippingStatus(ShippingStatus.SHIPPING);
        shipping.setShippingDate(new Date()); // Ghi nhận thời gian bắt đầu giao

        // Lưu Shipping trước, sau đó lưu BookOrder
        shippingRepository.save(shipping);
        bookOrderRepository.save(order); // Lưu BookOrder để đảm bảo liên kết

        return true;
    }

    @Override
    public boolean markDelivered(String orderId) {
        Optional<BookOrder> optionalOrder = bookOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return false;
        }

        BookOrder order = optionalOrder.get();
        Shipping shipping = order.getShipping();
        if (shipping == null) {
            return false;
        }

        shipping.setShippingStatus(ShippingStatus.DELIVERED);
        shipping.setDeliveredDate(new Date());
        shippingRepository.save(shipping);

        order.setOrderStatus(OrderStatus.COMPLETED);
        bookOrderRepository.save(order);
        return true;
    }

    @Override
    public boolean markFailed(String orderId, String reason) {
        Optional<BookOrder> optionalOrder = bookOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return false;
        }

        BookOrder order = optionalOrder.get();
        Shipping shipping = order.getShipping();
        if (shipping == null) {
            return false;
        }

        shipping.setShippingStatus(ShippingStatus.FAILED);
        shipping.setDeliveredDate(new Date());
        shipping.setNote(reason);
        shippingRepository.save(shipping);

        order.setOrderStatus(OrderStatus.CANCELLED);
        bookOrderRepository.save(order);
        return true;
    }
}