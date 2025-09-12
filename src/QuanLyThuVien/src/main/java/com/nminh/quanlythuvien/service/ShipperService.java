package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.entity.Shipping;

import java.util.List;

public interface ShipperService {

    // Lấy thông tin shipper
    Shipper getShipperById(String id);

    // Lấy các đơn hàng của shipper với trạng thái SHIPPING
    List<Shipping> getShippingOrders(String shipperId);

    // Shipper nhận đơn (→ PENDING)
    void acceptShipping(String shippingId);

    // Bắt đầu giao hàng (→ SHIPPING)
    void startShipping(String shippingId);

    // Giao hàng thành công (→ DELIVERED)
    void markAsDelivered(String shippingId);

    // Giao hàng thất bại (→ FAILED)
    void cancelShipping(String shippingId, String reason);
}
