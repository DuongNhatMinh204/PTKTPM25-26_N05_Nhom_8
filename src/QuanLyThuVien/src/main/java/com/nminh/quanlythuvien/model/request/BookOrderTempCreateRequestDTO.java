package com.nminh.quanlythuvien.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookOrderTempCreateRequestDTO {
    private String bookId;

    private String userId;

    private Integer quantity;
}
