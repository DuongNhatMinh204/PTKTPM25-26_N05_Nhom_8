package com.nminh.quanlythuvien.model.request;

import com.nminh.quanlythuvien.enums.PaymentType;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookOrderCreateRequest {
    private String userId;
    private String address;
    private PaymentType paymentType;
    private List<OrderItemRequest> orderItems;
}

