package com.nminh.quanlythuvien.config;

import com.nminh.quanlythuvien.constant.Constants;
import com.nminh.quanlythuvien.entity.User;
import com.nminh.quanlythuvien.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner createDefaultUser(UserRepository userRepository) {
        return args -> {
            if(!userRepository.existsByPhone("0000000000")){
                User user = new User();
                user.setPhone("0000000000");
                user.setPassword("123456");
                user.setStatus(1);
                user.setRole(Constants.ROLE_ADMIN);
                user.setBirthday(LocalDate.parse("2000-01-01"));
                user.setFullName("ADMIN");
                user.setEmail("admin@admin.com");
                user.setGender("Male");
                userRepository.save(user);
            }
            if(!userRepository.existsByPhone("0000000001")){
                User user = new User();
                user.setPhone("0000000001");
                user.setPassword("123456");
                user.setStatus(1);
                user.setRole(Constants.ROLE_STORE_KEEPER);
                user.setBirthday(LocalDate.parse("2000-01-01"));
                user.setFullName("STOREKEEPER");
                user.setEmail("store@admin.com");
                user.setGender("Male");
                userRepository.save(user);
            }
        };
    }
}