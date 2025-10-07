package com.nminh.quanlythuvien.repository;
import com.nminh.quanlythuvien.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipperRepository extends JpaRepository<Shipper, String> {
    Shipper findByShipperPhone(String shipperPhone);
}
