package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;

//    @Column(name = "role_name", nullable = false, unique = true, length = 100)
    @Column(name = "role_name")
    private String roleName;
}
