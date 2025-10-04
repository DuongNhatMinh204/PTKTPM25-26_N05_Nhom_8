package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.BookOrderCreateRequest;
import com.nminh.quanlythuvien.model.response.BookOrderDetailResponse;
import com.nminh.quanlythuvien.model.response.BookOrderResponse;
import com.nminh.quanlythuvien.entity.BookOrder;


import java.util.List;

public interface BookOrderService {
    void confirmOrder(String id);
    void cancelOrder(String id, String cancelReason);
    List<BookOrderResponse> orderPendingList();
    String createBookOrder(BookOrderCreateRequest request);
    String confirmBookOrderToAproved(String id);
    List<BookOrderDetailResponse> orderDetailList(String id);
    List<BookOrder> getOrdersWithShippingStatusAndNoShipper();
    List<BookOrder> getOrdersByShipper(String shipperId);
}
