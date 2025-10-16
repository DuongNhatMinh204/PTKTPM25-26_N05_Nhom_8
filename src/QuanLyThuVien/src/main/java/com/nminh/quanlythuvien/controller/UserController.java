package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.constant.MessageConstant;
import com.nminh.quanlythuvien.model.request.UserSignInRequestDTO;
import com.nminh.quanlythuvien.model.request.UserSignUpRequestDTO;
import com.nminh.quanlythuvien.model.response.ApiResponse;
import com.nminh.quanlythuvien.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ApiResponse signup(@RequestBody UserSignUpRequestDTO userSignUpDTO) {
        log.info("User signup : {}", userSignUpDTO);
        ApiResponse apiResponse = new ApiResponse(1001, MessageConstant.MESSAGE_SIGN_UP_SUCCESSFUL,userService.signUp(userSignUpDTO));
        return apiResponse;
    }

    @PostMapping("/signin")
    public ApiResponse signin(@RequestBody UserSignInRequestDTO userSignInDTO) {
        log.info("User signin : {}", userSignInDTO);
        ApiResponse apiResponse = new ApiResponse(1002,userService.signIn(userSignInDTO));
        return apiResponse;
    }

    @PutMapping("/change-status/{id}")
    public ApiResponse changeStatus(@PathVariable String id) {
        log.info("User lock : {}", id);
        ApiResponse apiResponse = new ApiResponse(1003,userService.changeStatus(id));
        return apiResponse;
    }

    @GetMapping("/get-all")
    public ApiResponse getAll() {
        log.info("User getAll");
        ApiResponse apiResponse = new ApiResponse(1004,userService.allUserInfo());
        return apiResponse;
    }
    @GetMapping("/get-all-acc")
    public ApiResponse getAllAccount() {
        log.info("User getAll");
        ApiResponse apiResponse = new ApiResponse(1004,userService.allDetail());
        return apiResponse;
    }
}
