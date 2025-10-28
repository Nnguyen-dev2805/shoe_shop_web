package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * ReturnShipment - Quản lý việc shipper đi lấy hàng trả về từ khách hàng
 * Khác với Shipment thông thường (giao hàng đi)
 */
@Entity
@Table(name = "return_shipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnShipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "return_request_id", nullable = false, unique = true)
    private ReturnRequest returnRequest;
    
    @ManyToOne
    @JoinColumn(name = "shipper_id", referencedColumnName = "id", nullable = false)
    private Users shipper;  // Shipper đi lấy hàng
    
    @ManyToOne
    @JoinColumn(name = "pickup_address_id", nullable = false)
    private Address pickupAddress;  // Địa chỉ lấy hàng (address của customer)
    
    @Column(name = "status", length = 50)
    private String status;  // PENDING, PICKED_UP, IN_TRANSIT, DELIVERED_TO_WAREHOUSE
    
    @Column(name = "pickup_date")
    private Date pickupDate;  // Ngày shipper lấy hàng
    
    @Column(name = "delivery_date")
    private Date deliveryDate;  // Ngày giao về kho
    
    @Column(name = "created_date")
    private Date createdDate;
    
    @Column(name = "updated_date")
    private Date updatedDate;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;  // Ghi chú của shipper
    
    @Column(name = "proof_images", columnDefinition = "TEXT")
    private String proofImages;  // Ảnh chứng minh đã lấy hàng
    
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
        updatedDate = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }
}
