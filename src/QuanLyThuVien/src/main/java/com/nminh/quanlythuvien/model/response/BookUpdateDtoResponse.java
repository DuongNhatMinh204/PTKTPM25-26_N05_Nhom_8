package com.nminh.quanlythuvien.model.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookUpdateDtoResponse {
    private String bookName;

    private String authorship;

    private String bookGerne;

    private String bookPublisher;

    private Integer quantity;

    private Double price;

    private String imageUrl;
}
