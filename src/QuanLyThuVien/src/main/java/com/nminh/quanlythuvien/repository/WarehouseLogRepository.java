package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.WarehouseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseLogRepository extends JpaRepository<WarehouseLog, String> {
}
