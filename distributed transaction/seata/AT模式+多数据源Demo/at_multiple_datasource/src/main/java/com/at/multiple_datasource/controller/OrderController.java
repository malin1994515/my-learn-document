package com.at.multiple_datasource.controller;

import com.at.multiple_datasource.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/create")
    public Integer createOrder(@RequestParam Long userId,
                               @RequestParam Long productId,
                               @RequestParam Integer price) throws Exception {
        log.info("[createOrder] 收到下单请求，用户{},商品{},价格{}", userId, productId, price);
        return orderService.createOrder(userId, productId, price);
    }
}
