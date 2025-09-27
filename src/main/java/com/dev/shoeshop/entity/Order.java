package com.dev.shoeshop.entity;


import com.dev.shoeshop.enums.PayOption;
import com.dev.shoeshop.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
//    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "customer_id", nullable = false) // Khóa ngoại đến User
    private Users user;

    @Column(name = "total_price", nullable = false)
//    @NotNull(message = "Total price cannot be null")
    private Double totalPrice;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // Định dạng DateTime
    private Date createdDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('IN_STOCK', 'SHIPPED', 'DELIVERED', 'CANCEL', 'RETURN')", nullable = false)
    private ShipmentStatus status;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderDetail> orderDetailSet;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_option", columnDefinition = "ENUM('COD', 'VNPAY')", nullable = false)
    private PayOption payOption;


    @ManyToOne
//    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "delivery_address_id", nullable = false) // Khóa ngoại đến User
    private addressEntity address;


}
