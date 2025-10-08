package com.dev.shoeshop.dto.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BrandRequest {
    private Long id;
    
    @NotBlank(message = "Tên brand không được để trống")
    @Size(max = 100, message = "Tên brand không được vượt quá 100 ký tự")
    private String name;
}
