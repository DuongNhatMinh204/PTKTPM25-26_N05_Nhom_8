package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.BookOrderCreateRequest;
import com.nminh.quanlythuvien.model.response.BookOrderResponse;

import java.util.List;

public interface BookOrderService {
    void confirmOrder(String id);
    void cancelOrder(String id, String cancelReason);
    List<BookOrderResponse> orderPendingList();
    String createBookOrder(BookOrderCreateRequest request);
}
