package com.nminh.quanlythuvien.repository;

import com.nminh.quanlythuvien.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipperRepository extends JpaRepository<Shipper, String> {
    Optional<Shipper> findByShipperPhone(String shipperPhone);
}
