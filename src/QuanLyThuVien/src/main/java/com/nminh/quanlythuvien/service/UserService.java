package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.UserSignInRequestDTO;
import com.nminh.quanlythuvien.model.request.UserSignUpRequestDTO;
import com.nminh.quanlythuvien.model.response.UserSignInResponseDTO;
import com.nminh.quanlythuvien.model.response.UserSignUpResponseDTO;


public interface UserService {
    UserSignUpResponseDTO signUp(UserSignUpRequestDTO userSignUpRequestDTO);
    UserSignInResponseDTO signIn(UserSignInRequestDTO userSignInRequestDTO);
    String changeStatus(String userId);
}
