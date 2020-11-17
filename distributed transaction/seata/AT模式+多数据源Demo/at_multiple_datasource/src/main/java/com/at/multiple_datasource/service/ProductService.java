package com.at.multiple_datasource.service;

public interface ProductService {
    void reduceStock(Long productId, Integer amount) throws Exception;
}
