package com.nminh.quanlythuvien.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nminh.quanlythuvien.enums.OrderStatus;
import com.nminh.quanlythuvien.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String address;

    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String cancelReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "orderId")
    @JsonIgnore
    private List<OrderDetail> orderDetail;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_id",referencedColumnName = "id")
    @JsonIgnore
    private Shipping shipping;

}
