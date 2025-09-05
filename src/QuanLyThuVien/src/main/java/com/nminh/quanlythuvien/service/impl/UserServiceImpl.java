package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.constant.Constants;
import com.nminh.quanlythuvien.entity.User;
import com.nminh.quanlythuvien.enums.ErrorCode;
import com.nminh.quanlythuvien.exception.AppException;
import com.nminh.quanlythuvien.model.request.UserSignUpRequestDTO;
import com.nminh.quanlythuvien.model.response.UserSignUpResponseDTO;
import com.nminh.quanlythuvien.repository.UserRepository;
import com.nminh.quanlythuvien.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserSignUpResponseDTO signUp(UserSignUpRequestDTO userSignUpRequestDTO) {
        if(!userSignUpRequestDTO.getPassword().equals(userSignUpRequestDTO.getConfirmPassword())){
            throw new AppException(ErrorCode.CONFIRM_PASSWORD_NOT_MATCH);
        }
        List<User> users = userRepository.findAll();
        for(User user : users){
            if (user.getPhone().equals(userSignUpRequestDTO.getPhone())){
                throw new AppException(ErrorCode.PHONE_EXISTED);
            }
        }
        User user = new User();

        user.setPhone(userSignUpRequestDTO.getPhone());
        user.setPassword(userSignUpRequestDTO.getPassword());
        user.setBirthday(userSignUpRequestDTO.getBirthday());
        user.setEmail(userSignUpRequestDTO.getEmail());
        user.setFullName(userSignUpRequestDTO.getFullName());
        user.setGender(userSignUpRequestDTO.getGender());
        user.setRole(Constants.ROLE_USER);
        user.setStatus(Constants.ACTIVE_STATUS);

        userRepository.save(user);

        return new UserSignUpResponseDTO(user.getPhone(),user.getFullName());

    }
}
