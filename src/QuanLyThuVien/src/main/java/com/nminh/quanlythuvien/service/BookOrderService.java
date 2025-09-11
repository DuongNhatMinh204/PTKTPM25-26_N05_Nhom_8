package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.repository.BookOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookOrderService {

    @Autowired
    private BookOrderRepository bookOrderRepository;

    public List<BookOrder> getApprovedOrders() {
        return bookOrderRepository.findByOrderStatus(OrderStatus.APPROVED);
    }

    public void confirmOrder(String id) {
        BookOrder order = bookOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getOrderStatus() != OrderStatus.APPROVED) {
            throw new RuntimeException("Order is not in APPROVED status");
        }
        order.setOrderStatus(OrderStatus.SHIPPING);
        bookOrderRepository.save(order);
    }

    public void cancelOrder(String id, String cancelReason) {
        BookOrder order = bookOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getOrderStatus() != OrderStatus.APPROVED) {
            throw new RuntimeException("Order is not in APPROVED status");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelReason(cancelReason);
        bookOrderRepository.save(order);
    }
}