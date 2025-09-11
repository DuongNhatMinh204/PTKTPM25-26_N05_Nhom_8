package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.UserSignInRequestDTO;
import com.nminh.quanlythuvien.model.request.UserSignUpRequestDTO;
import com.nminh.quanlythuvien.model.response.UserSignInResponseDTO;
import com.nminh.quanlythuvien.model.response.UserSignUpResponseDTO;


public interface UserService {
    public UserSignUpResponseDTO signUp(UserSignUpRequestDTO userSignUpRequestDTO);
    public UserSignInResponseDTO signIn(UserSignInRequestDTO userSignInRequestDTO);
}
