package com.nminh.quanlythuvien.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    CONFIRM_PASSWORD_NOT_MATCH(1001,"Confirm password not match", HttpStatus.BAD_REQUEST),
    ARGUMENT_NOT_VALID(1002,"This argument must be not blank or not null", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(1003,"Phone number existed", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_EXISTED(1004,"Account not existed", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1005,"Password incorrect", HttpStatus.BAD_REQUEST),
    PRICE_NOT_VALID(1006,"Price must be >= 0 ", HttpStatus.BAD_REQUEST),
    BOOK_NOT_EXISTED(1007,"Book not existed", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_STOCK(1008,"Not enough stock", HttpStatus.BAD_REQUEST),
    BOOK_ORDER_NOT_EXISTED(1009,"Book order not existed", HttpStatus.BAD_REQUEST),
    ORDER_DETAIL_NOT_EXISTED(1010,"Order detail not existed", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(1011,"Account locked", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

}
