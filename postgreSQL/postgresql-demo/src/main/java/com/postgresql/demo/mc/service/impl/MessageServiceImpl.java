package com.postgresql.demo.mc.service.impl;

import com.postgresql.demo.mc.entity.Message;
import com.postgresql.demo.mc.mapper.MessageMapper;
import com.postgresql.demo.mc.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author malin
 * @since 2020-11-22
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

}
