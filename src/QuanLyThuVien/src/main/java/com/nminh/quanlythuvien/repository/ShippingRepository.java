package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingRepository extends JpaRepository<Shipping, String> {

    // Lấy tất cả đơn shipping của 1 shipper
    List<Shipping> findByShipper_Id(String shipperId);

    // Lọc đơn shipping theo shipper + trạng thái
    List<Shipping> findByShipper_IdAndShippingStatus(String shipperId, ShippingStatus shippingStatus);
}
