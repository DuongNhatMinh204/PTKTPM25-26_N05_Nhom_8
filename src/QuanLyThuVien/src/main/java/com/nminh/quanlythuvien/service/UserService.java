package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.UserSignInRequestDTO;
import com.nminh.quanlythuvien.model.request.UserSignUpRequestDTO;
import com.nminh.quanlythuvien.model.response.UserDetailResponse;
import com.nminh.quanlythuvien.model.response.UserInfoResponse;
import com.nminh.quanlythuvien.model.response.UserSignInResponseDTO;
import com.nminh.quanlythuvien.model.response.UserSignUpResponseDTO;

import java.util.List;


public interface UserService {
    UserSignUpResponseDTO signUp(UserSignUpRequestDTO userSignUpRequestDTO);
    UserSignInResponseDTO signIn(UserSignInRequestDTO userSignInRequestDTO);
    String changeStatus(String userId);
    List<UserInfoResponse> allUserInfo();
    List<UserDetailResponse> allDetail();
}
