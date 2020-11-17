package com.at.multiple_datasource.service.impl;

import com.at.multiple_datasource.dao.OrderDao;
import com.at.multiple_datasource.entity.OrderDO;
import com.at.multiple_datasource.service.AccountService;
import com.at.multiple_datasource.service.OrderService;
import com.at.multiple_datasource.service.ProductService;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;
    @Resource
    private AccountService accountService;
    @Resource
    private ProductService productService;


    @Override
    @DS(value = "orderDS")  // dynamic datasource 选择 orderDS
    @GlobalTransactional    // 开启全局事务
    public Integer createOrder(Long userId, Long productId, Integer price) throws Exception {
        log.info("[createOrder]当前XID{}", RootContext.getXID());
        Integer amount = 1; // 购买数量

        // 3 扣减库存
        productService.reduceStock(productId, amount);

        // 4 扣减余额
        accountService.reduceBalance(userId, price);

        // 5 保存订单
        OrderDO order = OrderDO.builder()
                .userId(userId).productId(productId).payAmount(amount * price)
                .build();
        orderDao.saveOrder(order);
        log.info("[createOrder]保存订单:{}", order.getId());

        // 返回订单编号
        return order.getId();
    }
}
