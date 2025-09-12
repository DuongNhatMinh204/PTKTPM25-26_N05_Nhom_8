package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.enums.ShippingStatus;

import java.util.List;

public interface ShippingService {
    List<Shipping> getShippingsByShipperId(String shipperId);
    List<Shipping> getShippingsByShipperIdAndStatus(String shipperId, ShippingStatus status);
}
