package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @ManyToOne  // Hiện tại một người chỉ có 1 role
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

//    // Quan hệ OneToMany với Rating
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Rating> ratings = new ArrayList<>();

}
