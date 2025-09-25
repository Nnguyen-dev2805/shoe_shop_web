package com.dev.shoeshop.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CategoryCreateRequest {
    @NotNull(message = "Tên không được để trống")
    @Size(min = 1, max = 255, message = "Tên phải có độ dài từ 1 đến 255 ký tự")
    private String name;
    @Size(max = 255, message = "Mô tả không được quá 255 ký tự")
    private String description;
}
