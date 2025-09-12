package com.nminh.quanlythuvien.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignInResponseDTO {
    private String id;
    private String phone;
    private String role;
}
