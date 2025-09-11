package com.nminh.quanlythuvien.entity;

import com.nminh.quanlythuvien.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date shippingDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredDate;

    private String note;

    @ManyToOne
    @JoinColumn(name = "shipper_id")
    private Shipper shipper;

    @OneToOne(mappedBy = "shipping")
    private BookOrder bookOrder;
}
