package com.at.multiple_datasource.service;

public interface OrderService {
    /**
     * 创建订单
     *
     * @param userId    用户id
     * @param productId 产品id
     * @param price     价格
     * @return 受影响行数
     */
    Integer createOrder(Long userId, Long productId, Integer price) throws Exception;
}
