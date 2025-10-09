package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.constant.Constants;
import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.Shipper;
import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.entity.User;
import com.nminh.quanlythuvien.enums.ErrorCode;
import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import com.nminh.quanlythuvien.exception.AppException;
import com.nminh.quanlythuvien.model.request.ShipperCreateRequest;
import com.nminh.quanlythuvien.repository.BookOrderRepository;
import com.nminh.quanlythuvien.repository.ShipperRepository;
import com.nminh.quanlythuvien.repository.ShippingRepository;
import com.nminh.quanlythuvien.repository.UserRepository;
import com.nminh.quanlythuvien.service.ShipperService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ShipperServiceImpl implements ShipperService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShipperRepository shipperRepository;
    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private BookOrderRepository bookOrderRepository;

    @Override
    public Object createShipper(ShipperCreateRequest shipperCreateRequest) {
        User user = new User();

        user.setPhone(shipperCreateRequest.getPhone());
        user.setEmail(shipperCreateRequest.getEmail());
        user.setPassword(shipperCreateRequest.getPassword());
        user.setGender(shipperCreateRequest.getGender());
        user.setRole(Constants.ROLE_SHIPPER);
        user.setFullName(shipperCreateRequest.getFullName());
        user.setStatus(Constants.ACTIVE_STATUS);
        userRepository.save(user);

        Shipper shipper = new Shipper();
        shipper.setShipperPhone(shipperCreateRequest.getPhone());
        shipper.setShipperName(shipperCreateRequest.getFullName());
        shipperRepository.save(shipper);

        return shipper;
    }

    @Override
    public Object getAllShipper() {
        return shipperRepository.findAll();
    }

    @Override
    public Object getAllBookOrder(String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Shipper shipper = shipperRepository.findByShipperPhone(user.getPhone());
        return shippingRepository.findByShipper_Id(shipper.getId());
    }

    @Override
    public Object startShipping(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId).orElseThrow();
        shipping.setShippingStatus(ShippingStatus.SHIPPING);
        shippingRepository.save(shipping);
        return shipping;
    }

    @Override
    public Object delivered(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId).orElseThrow();
        shipping.setShippingStatus(ShippingStatus.DELIVERED);
        shippingRepository.save(shipping);

        BookOrder bookOrder = bookOrderRepository.findByShipping(shipping);
        bookOrder.setOrderStatus(OrderStatus.COMPLETED);
        bookOrderRepository.save(bookOrder);

        return shipping;
    }

    @Override
    public Object failed(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId).orElseThrow();
        shipping.setShippingStatus(ShippingStatus.FAILED);
        shippingRepository.save(shipping);

        BookOrder bookOrder = bookOrderRepository.findByShipping(shipping);
        bookOrder.setOrderStatus(OrderStatus.CANCELLED);
        bookOrderRepository.save(bookOrder);
        return shipping;
    }
}
