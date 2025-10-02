package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.repository.ShipperRepository;
import com.nminh.quanlythuvien.service.ShipperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShipperServiceImpl implements ShipperService {

    @Autowired
    private ShipperRepository shipperRepository;

    @Override
    public Optional<Shipper> getShipperByPhone(String phone) {
        return shipperRepository.findByShipperPhone(phone);
    }

    @Override
    public Optional<Shipper> getShipperById(String id) {
        return shipperRepository.findById(id);
    }
}
