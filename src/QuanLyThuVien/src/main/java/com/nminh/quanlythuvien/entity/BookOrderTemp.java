package com.nminh.quanlythuvien.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookOrderTemp {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String Id;

    private String bookId;

    private String userId;

    private Integer quantity;

    private boolean status;

}
