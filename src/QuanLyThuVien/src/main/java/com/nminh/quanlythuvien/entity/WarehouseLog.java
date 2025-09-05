package com.nminh.quanlythuvien.entity;

import com.nminh.quanlythuvien.enums.ActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private Integer preQuantity;

    private Integer currentQuantity;

    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDate;

    private String note;

}
