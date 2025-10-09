package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.entity.Shipping;
import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.model.response.BookOrderResponse;
import com.nminh.quanlythuvien.model.response.DailyRevenueProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookOrderRepository extends JpaRepository<BookOrder, String> {
    List<BookOrder> findByOrderStatus(OrderStatus orderStatus);
    List<BookOrder> findByShipping_Shipper_IdAndOrderStatus(String shipperId, OrderStatus orderStatus);

    @Query(value = "Select SUM(total_price) as totalRevenue " +
            "From book_order bo " +
            "where bo.order_status = 'COMPLETED' " +
            "and year(order_date) = :year " +
            "and month(order_date) = :month " +
            "GROUP BY YEAR(order_date), MONTH(order_date)",nativeQuery=true)
    Optional<Double> totalRevenueByMonth(int month, int year);

    @Query(value = "WITH RECURSIVE days AS ( " +
            "    SELECT 1 AS day " +
            "    UNION ALL " +
            "    SELECT day + 1 " +
            "    FROM days " +
            "    WHERE day + 1 <= DAY(LAST_DAY(CONCAT(:year, '-', :month, '-01'))) " +
            ")" +
            "SELECT " +
            "    d.day, " +
            "    COALESCE(SUM(bo.total_price), 0) AS revenue " +
            "FROM days d " +
            "LEFT JOIN book_order bo " +
            "    ON DAY(bo.order_date) = d.day " +
            "   AND MONTH(bo.order_date) = :month " +
            "   AND YEAR(bo.order_date) = :year" +
            "   AND bo.order_status = 'COMPLETED' " +
            "GROUP BY d.day\n" +
            "ORDER BY d.day;\n",nativeQuery=true)
    List<DailyRevenueProjection> dailyRevenue(int month, int year);

    @Query(value = "SELECT new com.nminh.quanlythuvien.model.response.BookOrderResponse(b.id, b.address,b.totalPrice, b.paymentType, b.orderDate, b.orderStatus , b.cancelReason) " +
            " FROM BookOrder b WHERE b.user.id = :userId ")
    List<BookOrderResponse> findByUser(@Param("userId") String userId);

    @Query("SELECT b FROM BookOrder b WHERE b.shipping = :shipping")
    BookOrder findByShipping(@Param("shipping") Shipping shipping);
}