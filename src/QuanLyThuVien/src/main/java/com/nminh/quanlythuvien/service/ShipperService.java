package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.model.request.ShipperCreateRequest;

import java.util.List;

public interface ShipperService {
    Object createShipper(ShipperCreateRequest shipperCreateRequest);
    Object getAllShipper();
    Object getAllBookOrder(String userId);
    Object startShipping(String shippingId);

    Object delivered(String shippingId);

    Object failed(String shippingId);
}
