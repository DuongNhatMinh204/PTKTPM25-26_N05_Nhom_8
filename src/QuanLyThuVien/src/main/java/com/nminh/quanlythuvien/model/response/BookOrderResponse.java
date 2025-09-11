package com.nminh.quanlythuvien.model.response;

import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.enums.PaymentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookOrderResponse {
    private String id;

    private String address;

    private Double totalPrice;

    private PaymentType paymentType;

    private Date orderDate;

    private OrderStatus orderStatus;

    private String cancelReason;
}
