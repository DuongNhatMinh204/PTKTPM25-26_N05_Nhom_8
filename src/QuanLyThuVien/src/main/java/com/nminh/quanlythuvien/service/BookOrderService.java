package com.nminh.quanlythuvien.service;

public interface BookOrderService {
    void confirmOrder(String id);
    void cancelOrder(String id, String cancelReason);
}
