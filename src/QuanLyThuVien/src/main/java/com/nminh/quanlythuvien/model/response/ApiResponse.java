package com.nminh.quanlythuvien.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int code = 1000;
    private String message;
    private T data;

    public ApiResponse(T data) {
        this.data = data;
    }
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public ApiResponse( String message, T data) {
        this.message = message;
        this.data = data;
    }
   public ApiResponse(int code, T data) {
        this.code = code;
        this.data = data;
   }
}
