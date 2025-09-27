package com.dev.shoeshop.dto;

import com.dev.shoeshop.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String fullname;

    private String email;

    private String address;

    private String phone;
}
