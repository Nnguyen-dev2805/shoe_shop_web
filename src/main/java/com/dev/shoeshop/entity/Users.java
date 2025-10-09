package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullname;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone", length = 10)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // ⚡ THÊM: OAuth2 fields
    @Column(name = "provider", length = 50)
    private String provider; // GOOGLE, FACEBOOK, LOCAL
    
    @Column(name = "profile_picture", length = 500)
    private String profilePicture;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> userAddresses = new ArrayList<>();
}
