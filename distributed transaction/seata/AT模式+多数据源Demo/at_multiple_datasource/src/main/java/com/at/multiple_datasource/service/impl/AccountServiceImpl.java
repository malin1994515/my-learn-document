package com.at.multiple_datasource.service.impl;

import com.at.multiple_datasource.dao.AccountDao;
import com.at.multiple_datasource.service.AccountService;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountDao accountDao;

    @Override
    @DS(value = "account-ds")   // 1. dynamic datasource 选择 account-ds
    @Transactional(propagation = Propagation.REQUIRES_NEW)  // 2. 开启新事务
    public void reduceBalance(Long userId, Integer price) throws Exception {
        log.info("[reduceBalance] 当前XID:{}", RootContext.getXID());

        // 3. 检查余额
        checkBalance(userId, price);
        log.info("[reduceBalance]开始扣减用户{}余额", userId);

        // 4. 扣减余额
        int updateCount = accountDao.reduceBalance(price);
        // 扣除失败
        if (updateCount == 0) {
            log.warn("[reduceBalance]扣除用户{}余额失败", userId);
            throw new Exception("余额不足");
        }
        // 扣除成功
        log.info("[reduceBalance]扣除用户{}余额成功", userId);
    }

    private void checkBalance(Long userId, Integer price) throws Exception {
        log.info("[checkBalance]检查用户{}余额", userId);
        Integer balance = accountDao.getBalance(userId);
        if (balance < price) {
            log.warn("[checkBalance]用户：{}余额不足，当前余额：{}", userId, balance);
            throw new Exception("余额不足");
        }
    }
}
