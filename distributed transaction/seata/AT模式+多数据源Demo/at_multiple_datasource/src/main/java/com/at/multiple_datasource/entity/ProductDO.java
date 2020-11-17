package com.at.multiple_datasource.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductDO {
    private Long id;
    private Integer stock;
    private LocalDateTime lastUpdateTime;
}
