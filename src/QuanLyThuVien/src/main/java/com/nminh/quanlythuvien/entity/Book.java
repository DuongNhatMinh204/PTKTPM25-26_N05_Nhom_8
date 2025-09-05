package com.nminh.quanlythuvien.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String bookName;

    private String authorship;

    private String bookGerne;

    private String bookPublisher;

    private Integer quantity;

    private Double price;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<OrderDetail> orderDetails;

}
