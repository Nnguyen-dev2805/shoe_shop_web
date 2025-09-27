package com.dev.shoeshop.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
//    @NotNull(message = "Order cannot be null")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "shipper_id", referencedColumnName = "id", nullable = false)
//    @NotNull(message = "Shipper must not be null")
    private Users shipper;

    @Column(name = "update_date")
    private Date updatedDate;


    @ManyToOne
//    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "shipment_address_id", nullable = false) // Khóa ngoại đến User
    private addressEntity address;

    @Column(name = "status")
    private String status;
}
