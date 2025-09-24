package com.nminh.quanlythuvien.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookOrderTempDetailDTO {
    private String id;

    private String book_id;

    private String bookUrl;

    private String bookName;

    private Double bookPrice;

    private Integer bookQuantity;

    private Double bookTotalPrice;
}
