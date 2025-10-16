package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.constant.Constants;
import com.nminh.quanlythuvien.entity.User;
import com.nminh.quanlythuvien.enums.ErrorCode;
import com.nminh.quanlythuvien.exception.AppException;
import com.nminh.quanlythuvien.mapper.UserMapper;
import com.nminh.quanlythuvien.model.request.UserSignInRequestDTO;
import com.nminh.quanlythuvien.model.request.UserSignUpRequestDTO;
import com.nminh.quanlythuvien.model.response.UserDetailResponse;
import com.nminh.quanlythuvien.model.response.UserInfoResponse;
import com.nminh.quanlythuvien.model.response.UserSignInResponseDTO;
import com.nminh.quanlythuvien.model.response.UserSignUpResponseDTO;
import com.nminh.quanlythuvien.repository.UserRepository;
import com.nminh.quanlythuvien.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

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

    @Override
    public UserSignInResponseDTO signIn(UserSignInRequestDTO userSignInRequestDTO) {
        User user = userRepository.findByPhone(userSignInRequestDTO.getPhone())
                .orElseThrow(()-> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        if(!user.getPassword().equals(userSignInRequestDTO.getPassword())){
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        if(user.getStatus().equals(Constants.INACTIVE_STATUS)){
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        return new UserSignInResponseDTO(user.getId(),user.getPhone(),user.getRole());
    }

    @Override
    public String changeStatus(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        assert user != null;
        if(user.getStatus().equals(Constants.ACTIVE_STATUS)){
            user.setStatus(Constants.INACTIVE_STATUS);
        }else {
            user.setStatus(Constants.ACTIVE_STATUS);
        }
        userRepository.save(user);
        return "Change status success";
    }

    @Override
    public List<UserInfoResponse> allUserInfo() {
        List<User> users = userRepository.findAll();
        List<UserInfoResponse> userInfoResponses = new ArrayList<>();
        for(User user : users){
            if(user.getPhone().equals("0000000000")){
                continue;
            }
            UserInfoResponse userInfoResponse = userMapper.toUserInfoResponse(user);
            userInfoResponses.add(userInfoResponse);
        }
        return userInfoResponses;
    }

    @Override
    public List<UserDetailResponse> allDetail() {
        List<User> users = userRepository.findAll();
        List<UserDetailResponse> responses = new ArrayList<>();
        for(User user : users){
            if(user.getPhone().equals("0000000000")){
                continue;
            }
            UserDetailResponse userDetailResponse = new UserDetailResponse();

            userDetailResponse.setId(user.getId());
            userDetailResponse.setPhone(user.getPhone());
            userDetailResponse.setFullName(user.getFullName());
            userDetailResponse.setRole(user.getRole());
            userDetailResponse.setStatus(user.getStatus());
            userDetailResponse.setEmail(user.getEmail());
            userDetailResponse.setGender(user.getGender());
            userDetailResponse.setBirthday(user.getBirthday());

            responses.add(userDetailResponse);

        }
        return responses;
    }
}
