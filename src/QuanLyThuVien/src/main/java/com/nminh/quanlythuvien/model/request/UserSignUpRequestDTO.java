package com.nminh.quanlythuvien.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequestDTO {
    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private String fullName;

    @Email(message = "Must be containing @ in email")
    private String email;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private  String phone;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private String password;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private String confirmPassword;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthday;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    private String gender;
}
