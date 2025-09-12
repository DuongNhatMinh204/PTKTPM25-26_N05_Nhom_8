package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import com.nminh.quanlythuvien.repository.ShippingRepository;
import com.nminh.quanlythuvien.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingRepository shippingRepository;

    @Override
    public List<Shipping> getShippingsByShipperId(String shipperId) {
        return shippingRepository.findByShipper_Id(shipperId);
    }

    @Override
    public List<Shipping> getShippingsByShipperIdAndStatus(String shipperId, ShippingStatus status) {
        return shippingRepository.findByShipper_IdAndShippingStatus(shipperId, status);
    }
}
