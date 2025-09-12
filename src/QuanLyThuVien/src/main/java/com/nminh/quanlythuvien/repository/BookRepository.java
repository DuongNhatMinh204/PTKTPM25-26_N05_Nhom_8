package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    Page<Book> findByStatus(Integer status, Pageable pageable);
}
