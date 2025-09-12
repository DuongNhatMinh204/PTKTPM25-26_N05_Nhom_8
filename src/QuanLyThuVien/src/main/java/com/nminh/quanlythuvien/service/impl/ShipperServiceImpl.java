package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import com.nminh.quanlythuvien.repository.ShipperRepository;
import com.nminh.quanlythuvien.repository.ShippingRepository;
import com.nminh.quanlythuvien.service.ShipperService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ShipperServiceImpl implements ShipperService {

    @Autowired
    private ShipperRepository shipperRepository;

    @Autowired
    private ShippingRepository shippingRepository;

    @Override
    public Shipper getShipperById(String id) {
        return shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipper không tồn tại"));
    }

    @Override
    public List<Shipping> getShippingOrders(String shipperId) {
        return shippingRepository.findByShipper_IdAndShippingStatus(shipperId, ShippingStatus.SHIPPING);
    }

    @Override
    @Transactional
    public void acceptShipping(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        shipping.setShippingStatus(ShippingStatus.PENDING);
        shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public void startShipping(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (shipping.getShippingStatus() != ShippingStatus.PENDING) {
            throw new RuntimeException("Đơn hàng chưa ở trạng thái PENDING");
        }

        shipping.setShippingStatus(ShippingStatus.SHIPPING);
        shipping.setShippingDate(new Date());
        shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public void markAsDelivered(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (shipping.getShippingStatus() != ShippingStatus.SHIPPING) {
            throw new RuntimeException("Đơn hàng chưa ở trạng thái SHIPPING");
        }

        shipping.setShippingStatus(ShippingStatus.DELIVERED);
        shipping.setDeliveredDate(new Date());
        shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public void cancelShipping(String shippingId, String reason) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (shipping.getShippingStatus() != ShippingStatus.SHIPPING &&
                shipping.getShippingStatus() != ShippingStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn khi đang PENDING hoặc SHIPPING");
        }

        shipping.setShippingStatus(ShippingStatus.FAILED);
        shipping.setNote(reason);
        shipping.setDeliveredDate(new Date());
        shippingRepository.save(shipping);
    }
}
