package com.nminh.quanlythuvien.model.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookOrderTempCreateResponseDTO {
    private String Id;

    private String bookId;

    private String userId;

    private Integer quantity;

    private boolean status;
}
