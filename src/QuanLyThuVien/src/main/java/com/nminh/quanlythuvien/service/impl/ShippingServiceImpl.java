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

        // ✅ Cập nhật vào đơn hàng (nếu quan hệ 2 chiều)
        order.setShipping(shipping);
        bookOrderRepository.save(order);

        return true;
    }
}
