package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.entity.Shipper;

import java.util.Optional;

public interface ShipperService {
    Optional<Shipper> getShipperByPhone(String phone);
    Optional<Shipper> getShipperById(String id);
}
