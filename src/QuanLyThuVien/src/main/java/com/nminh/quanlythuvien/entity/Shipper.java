package com.nminh.quanlythuvien.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Shipper {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String shipperName;

    private String shipperPhone;

    @OneToMany(mappedBy = "shipper")
    @JsonIgnore
    private List<Shipping> shippings;
}
