package com.nminh.quanlythuvien.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDTORequest {
    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private String bookName;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private String authorship;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private String bookGerne;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private String bookPublisher;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private Integer quantity;

    @Min(value = 0,message = "PRICE_NOT_VALID")
    private Double price;

    private String imageUrl;
}
