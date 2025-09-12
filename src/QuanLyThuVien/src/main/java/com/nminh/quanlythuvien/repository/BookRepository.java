package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    Page<Book> findByStatus(Integer status, Pageable pageable);

    @Query(value = "select " +
            "b.id," +
            "b.book_name," +
            "b.authorship," +
            "b.book_gerne," +
            "b.book_publisher," +
            "b.quantity," +
            "b.price," +
            "b.image_url," +
            "b.status " +
            "from book b " +
            "where b.status = 1 " +
            "and (b.book_name like :keyword " +
            "or b.authorship like :keyword " +
            "or b.book_gerne like :keyword " +
            "or b.book_publisher like :keyword) ",
    countQuery = "select count(b.id) " +
            "from book b " +
            "where b.status = 1 " +
            "and (b.book_name like :keyword " +
            "or b.authorship like :keyword " +
            "or b.book_gerne like :keyword " +
            "or b.book_publisher like :keyword) "
    , nativeQuery = true)
    Page<Book> findByKeyword(String keyword, Pageable pageable);
}
