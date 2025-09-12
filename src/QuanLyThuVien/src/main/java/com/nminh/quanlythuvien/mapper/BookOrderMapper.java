package com.nminh.quanlythuvien.mapper;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.model.response.BookOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class BookOrderMapper {
    public BookOrderResponse toBookOrderResponse(BookOrder bookOrder) {
        BookOrderResponse bookOrderResponse = new BookOrderResponse();

        bookOrderResponse.setId(bookOrder.getId());
        bookOrderResponse.setAddress(bookOrder.getAddress());
        bookOrderResponse.setTotalPrice(bookOrder.getTotalPrice());
        bookOrderResponse.setPaymentType(bookOrder.getPaymentType());
        bookOrderResponse.setOrderDate(bookOrder.getOrderDate());
        bookOrderResponse.setOrderStatus(bookOrder.getOrderStatus());
        bookOrderResponse.setCancelReason(bookOrder.getCancelReason());

        return bookOrderResponse;
    }
}
