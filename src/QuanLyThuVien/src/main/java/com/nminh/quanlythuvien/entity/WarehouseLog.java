package com.nminh.quanlythuvien.entity;

import com.nminh.quanlythuvien.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @CreationTimestamp
    private Date actionDate;

    private String note;

}
