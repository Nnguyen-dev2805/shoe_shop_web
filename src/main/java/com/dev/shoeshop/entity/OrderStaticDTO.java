package com.dev.shoeshop.entity;


import lombok.Data;

@Data
public class OrderStaticDTO {

    private long inStock;
    private long shipping;
    private long delivered;
    private long cancel;
    private long preturn;
}
