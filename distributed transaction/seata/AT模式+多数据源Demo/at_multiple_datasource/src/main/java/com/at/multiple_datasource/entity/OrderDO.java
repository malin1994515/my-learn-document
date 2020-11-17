package com.at.multiple_datasource.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderDO {
    private Integer id;
    private Long userId;
    private Long productId;
    private Integer payAmount;
    private LocalDateTime addTime;
    private LocalDateTime lastUpdateTime;
}
