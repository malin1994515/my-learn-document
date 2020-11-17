package com.at.multiple_datasource.service;

public interface AccountService {
    void reduceBalance(Long userId, Integer price) throws Exception;
}
