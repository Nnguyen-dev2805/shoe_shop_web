package com.dev.shoeshop.dto.brand;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BrandRequest {
    private Long id;
    private String name;
}
