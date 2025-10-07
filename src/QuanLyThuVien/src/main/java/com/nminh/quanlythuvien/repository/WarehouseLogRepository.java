package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.WarehouseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseLogRepository extends JpaRepository<WarehouseLog, String> {
    List<WarehouseLog> findByBook_Id(String bookId); // Lấy log theo mã sách nếu cần
    @Query(value = "select  sum(b.price*(wl.current_quantity-wl.pre_quantity)) as totalImportCost " +
            "from warehouse_log wl " +
            "left join book b on wl.book_id = b.id " +
            "where wl.action_type = 'IMPORT' " +
            "and year(action_date) = :year " +
            "and month(action_date) = :month ",nativeQuery = true)
    Optional<Double> totalImportCostByMonth(int month, int year);
}
