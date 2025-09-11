package com.nminh.quanlythuvien.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    private String imageUrl;

    private Integer status;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<WarehouseLog> warehouseLogs;
}
