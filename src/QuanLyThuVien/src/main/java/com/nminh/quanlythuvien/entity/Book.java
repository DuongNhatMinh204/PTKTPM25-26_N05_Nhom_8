package com.nminh.quanlythuvien.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<OrderDetail> orderDetails;

}
