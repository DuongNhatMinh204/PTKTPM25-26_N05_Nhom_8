package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.BookOrderTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookOrderTempRepository extends JpaRepository<BookOrderTemp, String> {
    List<BookOrderTemp> findByUserId(String userId);
}
