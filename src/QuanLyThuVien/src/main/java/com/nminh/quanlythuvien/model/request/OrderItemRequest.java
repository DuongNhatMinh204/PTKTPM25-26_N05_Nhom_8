package com.nminh.quanlythuvien.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {
    private String bookId;
    private Integer quantity;
    private String orderTempId;
}
