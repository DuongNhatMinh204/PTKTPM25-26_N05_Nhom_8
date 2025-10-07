package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.OrderDetail;
import com.nminh.quanlythuvien.model.response.BookStatistic;
import com.nminh.quanlythuvien.model.response.BookStatisticProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    Optional<List<OrderDetail>> findByOrderId(BookOrder orderId);
    @Query(value = "SELECT b.id as bookId ,b.book_name as bookName ,od.quantity as quantity " +
            "FROM order_detail od " +
            "left join book_order bo  on od.order_id = bo.id " +
            "left join book b on od.book_id = b.id " +
            "where year(bo.order_date) = :year " +
            "and month(bo.order_date) = :month " +
            "and bo.order_status = 'COMPLETED' " +
            "order by od.quantity desc " +
            "limit 1",nativeQuery = true)
    BookStatisticProjection findBookBestSeller(int month, int year);

    @Query(value = "SELECT b.id as bookId ,b.book_name as bookName ,od.quantity as quantity " +
            "FROM order_detail od " +
            "left join book_order bo  on od.order_id = bo.id " +
            "left join book b on od.book_id = b.id " +
            "where year(bo.order_date) = :year " +
            "and month(bo.order_date) = :month " +
            "and bo.order_status = 'COMPLETED' " +
            "order by od.quantity asc " +
            "limit 1",nativeQuery = true)
    BookStatisticProjection findBookWorthSeller(int month, int year);
}
