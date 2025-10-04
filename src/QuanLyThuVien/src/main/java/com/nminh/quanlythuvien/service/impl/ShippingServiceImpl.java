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
import java.util.Date;  // cho lớp Date
import com.nminh.quanlythuvien.enums.OrderStatus;  // cho enum OrderStatus


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

        // ✅ Nếu đơn hàng chưa có Shipping -> tạo mới
        if (shipping == null) {
            shipping = new Shipping();
            shipping.setBookOrder(order);
            shipping.setShippingStatus(ShippingStatus.PENDING);
        }

        // ❌ Nếu shipping đã có shipper rồi thì không được nhận lại
        if (shipping.getShipper() != null) {
            return false;
        }

        // ✅ Gán shipper và cập nhật trạng thái
        shipping.setShipper(shipper);
        shipping.setShippingStatus(ShippingStatus.SHIPPING);
        shippingRepository.save(shipping);
        order.setOrderStatus(OrderStatus.SHIPPING);

        shippingRepository.save(shipping);
        bookOrderRepository.save(order);

        // ✅ Cập nhật vào đơn hàng (nếu quan hệ 2 chiều)
        order.setShipping(shipping);
        bookOrderRepository.save(order);

        return true;
    }
    @Override
    public boolean markDelivered(String orderId) {
        Optional<BookOrder> optionalOrder = bookOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) return false;

        BookOrder order = optionalOrder.get();
        Shipping shipping = order.getShipping();
        if (shipping == null) return false;

        shipping.setShippingStatus(ShippingStatus.DELIVERED);
        shipping.setDeliveredDate(new Date());
        shippingRepository.save(shipping);

        order.setOrderStatus(OrderStatus.COMPLETED);
        bookOrderRepository.save(order);
        return true;
    }

    @Override
    public boolean markFailed(String orderId, String reason) {
        Optional<BookOrder> optionalOrder = bookOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) return false;

        BookOrder order = optionalOrder.get();
        Shipping shipping = order.getShipping();
        if (shipping == null) return false;

        shipping.setShippingStatus(ShippingStatus.FAILED);
        shipping.setDeliveredDate(new Date());
        shipping.setNote(reason);
        shippingRepository.save(shipping);

        order.setOrderStatus(OrderStatus.CANCELLED);
        bookOrderRepository.save(order);
        return true;
    }

}
