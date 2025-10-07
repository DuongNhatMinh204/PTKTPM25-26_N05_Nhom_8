package com.nminh.quanlythuvien.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookStatistic {
    private String bookId;
    private String bookName;
    private int quantity;
}
