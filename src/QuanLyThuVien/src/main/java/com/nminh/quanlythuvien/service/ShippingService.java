package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.entity.Shipper;

public interface ShippingService {
    boolean assignShippingToShipper(String orderId, Shipper shipper);
    boolean markDelivered(String orderId);
    boolean markFailed(String orderId, String reason);
}
