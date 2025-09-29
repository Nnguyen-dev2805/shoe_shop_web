package com.dev.shoeshop.dto.product;

import com.dev.shoeshop.dto.productdetail.ProductDetailRequest;
import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductRequest {
    protected Long id;
    protected Long categoryId;
    protected Long brandId;
    private String title;
    private String description;
    private Long voucher;
    private double price;
    private String categoryName;
    private String brandName;
    private List<ProductDetailRequest> productDetails;
}
