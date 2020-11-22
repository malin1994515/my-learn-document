package com.postgresql.demo.mc.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.postgresql.demo.mc.entity.Message;
import com.postgresql.demo.mc.service.IMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author malin
 * @since 2020-11-22
 */
@RestController
@RequestMapping("/mc/message")
public class MessageController {
    @Resource
    private IMessageService messageService;

    @GetMapping("/page")
    public IPage<Message> page(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam String beginDate,
                               @RequestParam String endDate) {
        IPage<Message> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.between("create_time", Timestamp.valueOf(beginDate), Timestamp.valueOf(endDate));
        return messageService.page(page, wrapper);
    }

    @PostMapping
    public boolean insert(@RequestBody Message message) {
        message.setId(UUID.randomUUID().toString());
        message.setCreateTime(LocalDateTime.now());
        return messageService.save(message);
    }
}

