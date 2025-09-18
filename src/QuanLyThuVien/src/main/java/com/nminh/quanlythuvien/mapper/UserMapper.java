package com.nminh.quanlythuvien.mapper;

import com.nminh.quanlythuvien.entity.User;
import com.nminh.quanlythuvien.model.response.UserInfoResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserInfoResponse toUserInfoResponse(User user) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();

        userInfoResponse.setPhone(user.getPhone());
        userInfoResponse.setFullName(user.getFullName());
        userInfoResponse.setStatus(user.getStatus());
        userInfoResponse.setRole(user.getRole());
        userInfoResponse.setEmail(user.getEmail());
        userInfoResponse.setGender(user.getGender());
        userInfoResponse.setBirthday(user.getBirthday());

        return userInfoResponse;
    }
}
