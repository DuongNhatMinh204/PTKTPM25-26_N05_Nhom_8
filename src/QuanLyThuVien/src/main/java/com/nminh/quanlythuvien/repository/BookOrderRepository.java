package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.BookOrder;
import com.nminh.quanlythuvien.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookOrderRepository extends JpaRepository<BookOrder, String> {

    // Lấy tất cả đơn theo trạng thái
    List<BookOrder> findByOrderStatus(OrderStatus orderStatus);

    // Lấy các đơn SHIPPING chưa có shipper
    List<BookOrder> findByOrderStatusAndShipping_ShipperIsNull(OrderStatus status);

    // ✅ Lấy danh sách đơn theo shipperId, dùng JOIN để đảm bảo shipper được fetch
    @Query("SELECT o FROM BookOrder o JOIN o.shipping s WHERE s.shipper.id = :shipperId")
    List<BookOrder> findOrdersByShipperId(@Param("shipperId") String shipperId);

    @Query("SELECT o FROM BookOrder o WHERE o.shipping.shipper.id = :shipperId AND o.shipping.shippingStatus = 'SHIPPING'")
    List<BookOrder> getOrdersByShipper(@Param("shipperId") String shipperId);

}
