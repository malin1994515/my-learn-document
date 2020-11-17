package com.at.multiple_datasource.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountDO {
    private Long id;
    private Integer balance;
    private LocalDateTime lastUpdateTime;
}
