package com.nminh.quanlythuvien.entity;

import jakarta.persistence.*;

@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Integer quantity;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private BookOrder orderId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
