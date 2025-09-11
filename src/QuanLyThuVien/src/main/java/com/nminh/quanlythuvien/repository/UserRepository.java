package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);
}
