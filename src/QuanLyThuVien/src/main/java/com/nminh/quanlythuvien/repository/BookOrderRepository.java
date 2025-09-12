package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookOrderRepository extends JpaRepository<BookOrder, String> {
    List<BookOrder> findByOrderStatus(OrderStatus orderStatus);
    List<BookOrder> findByShipping_Shipper_IdAndOrderStatus(String shipperId, OrderStatus orderStatus);
}