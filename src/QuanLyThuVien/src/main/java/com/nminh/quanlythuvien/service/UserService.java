package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.UserSignUpRequestDTO;
import com.nminh.quanlythuvien.model.response.UserSignUpResponseDTO;


public interface UserService {
    public UserSignUpResponseDTO signUp(UserSignUpRequestDTO userSignUpRequestDTO);
}
