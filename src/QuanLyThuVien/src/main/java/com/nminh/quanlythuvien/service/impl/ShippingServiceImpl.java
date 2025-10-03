package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import com.nminh.quanlythuvien.repository.BookOrderRepository;
import com.nminh.quanlythuvien.repository.ShippingRepository;
import com.nminh.quanlythuvien.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private BookOrderRepository bookOrderRepository;

    @Autowired
    private ShippingRepository shippingRepository;

    @Override
    public boolean assignShippingToShipper(String orderId, Shipper shipper) {
        Optional<BookOrder> optionalOrder = bookOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) return false;

        BookOrder order = optionalOrder.get();
        Shipping shipping = order.getShipping();

        if (shipping == null || shipping.getShipper() != null) return false;

        shipping.setShipper(shipper);
        shipping.setShippingStatus(ShippingStatus.SHIPPING);
        shippingRepository.save(shipping);
        return true;
    }
}
