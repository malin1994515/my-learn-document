package com.at.multiple_datasource.service.impl;

import com.at.multiple_datasource.dao.ProductDao;
import com.at.multiple_datasource.service.ProductService;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductDao productDao;

    @Override
    @DS(value = "product-ds")   // 1. dynamic datasource 选择 product-ds作为数据源
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 2. 开启新事务
    public void reduceStock(Long productId, Integer amount) throws Exception {
        log.info("[reduceStock] 当前XID: {}", RootContext.getXID());
        // 3. 检查库存
        checkStock(productId, amount);

        log.info("[reduceStock] 开始扣减{}库存", productId);
        // 4. 扣减库存
        int updateCount = productDao.reduceStock(productId, amount);
        // 扣除失败
        if (updateCount == 0) {
            log.warn("[reduceStock]扣除{}库存失败", productId);
            throw new Exception("库存不足");
        }
        // 扣除成功
        log.info("[reduceStock]扣除{}库存成功", productId);
    }

    private void checkStock(Long productId, Integer requiredAmount) throws Exception {
        log.info("[checkStock]检查{}库存", productDao);
        Integer stock = productDao.getStock(productId);
        if (stock < requiredAmount) {
            log.warn("[checkStock] {} 库存不足，当前库存: {}", productId, stock);
            throw new Exception("库存不足");
        }
    }
}
