package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.*;
import com.nminh.quanlythuvien.enums.ErrorCode;
import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.enums.ShippingStatus;
import com.nminh.quanlythuvien.exception.AppException;
import com.nminh.quanlythuvien.mapper.BookOrderMapper;
import com.nminh.quanlythuvien.model.request.BookOrderCreateRequest;
import com.nminh.quanlythuvien.model.request.OrderItemRequest;
import com.nminh.quanlythuvien.model.response.BookOrderDetailResponse;
import com.nminh.quanlythuvien.model.response.BookOrderResponse;
import com.nminh.quanlythuvien.repository.*;
import com.nminh.quanlythuvien.service.BookOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookOrderServiceImpl implements BookOrderService {

    @Autowired
    private BookOrderRepository bookOrderRepository;

    @Autowired
    private BookOrderMapper bookOrderMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BookOrderTempRepository bookOrderTempRepository;

    @Autowired
    private ShipperRepository shipperRepository;
    @Autowired
    private ShippingRepository shippingRepository;

    public List<BookOrder> getApprovedOrders() {
        return bookOrderRepository.findByOrderStatus(OrderStatus.APPROVED);
    }

    public void confirmOrder(String bookOrderId,String shipperId) {
        BookOrder order = bookOrderRepository.findById(bookOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));
        if (order.getOrderStatus() != OrderStatus.APPROVED) {
            throw new RuntimeException("Order is not in APPROVED status");
        }

        Shipping shipping = new Shipping();

        shipping.setShippingAddress(order.getAddress());
        shipping.setShippingDate(new Date());
        shipping.setShippingStatus(ShippingStatus.PENDING);
        shipping.setNote("");
        shipping.setShipper(shipper);

        shippingRepository.save(shipping);

        order.setShipping(shipping);
        order.setOrderStatus(OrderStatus.SHIPPING);
        bookOrderRepository.save(order);
    }

    public void cancelOrder(String id, String cancelReason) {
        BookOrder order = bookOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getOrderStatus() != OrderStatus.APPROVED) {
            throw new RuntimeException("Order is not in APPROVED status");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelReason(cancelReason);
        bookOrderRepository.save(order);
    }

    @Override
    public List<BookOrderResponse> orderPendingList() {
        List<BookOrder> orders = bookOrderRepository.findByOrderStatus(OrderStatus.PENDING);
        List<BookOrderResponse> responses = new ArrayList<BookOrderResponse>();
        for (BookOrder order : orders) {
            BookOrderResponse orderResponse = bookOrderMapper.toBookOrderResponse(order);
            responses.add(orderResponse);
        }
        return responses;
    }

    @Transactional
    @Override
    public String createBookOrder(BookOrderCreateRequest request) {
        // Tao book order
        BookOrder bookOrder = new BookOrder();

        bookOrder.setUser(userRepository.findById(request.getUserId()).orElseThrow(()->new AppException(ErrorCode.ACCOUNT_NOT_EXISTED)));
        bookOrder.setAddress(request.getAddress());
        bookOrder.setPaymentType(request.getPaymentType());
        bookOrder.setOrderStatus(OrderStatus.PENDING);
        bookOrder.setOrderDate(new Date());
        Double totalPrice = 0.0;

        bookOrderRepository.save(bookOrder);

        // Tạo OrderDetail cho từng sách
        for(OrderItemRequest item : request.getOrderItems()) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

            BookOrderTemp orderTemp = bookOrderTempRepository.findById(item.getOrderTempId()).orElse(null);
            orderTemp.setStatus(false);
            bookOrderTempRepository.save(orderTemp);

            if(book.getQuantity() < item.getQuantity()) {
                throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);
            }
            Double price = book.getPrice() * item.getQuantity();
            totalPrice += price;

            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setBook(book);
            orderDetail.setPrice(price);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setOrderId(bookOrder);

            orderDetailRepository.save(orderDetail);
        }
        // Update total price
        bookOrder.setTotalPrice(totalPrice);
        bookOrderRepository.save(bookOrder);

        return "Create Book Order Success";
    }

    @Override
    public String confirmBookOrderToAproved(String id) {
        BookOrder bookOrder = bookOrderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_ORDER_NOT_EXISTED));
        bookOrder.setOrderStatus(OrderStatus.APPROVED);
        bookOrderRepository.save(bookOrder);
        return "Confirm Book To Approved Order Success";
    }

    @Override
    public List<BookOrderDetailResponse> orderDetailList(String id) {
        BookOrder bookOrder = bookOrderRepository.findById(id).get();

        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(bookOrder)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));

        List<BookOrderDetailResponse> responses = new ArrayList<>();

        for(OrderDetail orderDetail : orderDetailList) {
            BookOrderDetailResponse orderDetailResponse = new BookOrderDetailResponse();

            Book book = orderDetail.getBook();

            orderDetailResponse.setId(book.getId());
            orderDetailResponse.setBookName(book.getBookName());
            orderDetailResponse.setPrice(book.getPrice());
            orderDetailResponse.setQuantity(orderDetail.getQuantity());
            orderDetailResponse.setAuthorship(book.getAuthorship());
            orderDetailResponse.setBookGerne(book.getBookGerne());
            orderDetailResponse.setBookPublisher(book.getBookPublisher());
            orderDetailResponse.setImageUrl(book.getImageUrl());

            responses.add(orderDetailResponse);
        }
        return responses;

    }

    @Override
    public Object getAll(String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return bookOrderRepository.findByUser(userId);
    }
}