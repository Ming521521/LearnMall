package com.example.mall.Listener;

import com.example.mall.pojo.PayInfo;
import com.example.mall.service.IOrderService;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ：Ming
 * @date ：Created in 2020/8/7 0007 21:19
 */
@Component
@RabbitListener(queues = "payNotify")
@Slf4j
public class PayMesListener {
    @Autowired
    private IOrderService orderService;
    @RabbitHandler
    public   void  process(String msg){
        PayInfo payInfo=new Gson().fromJson(msg,PayInfo.class);
        if (payInfo.getPlatformStatus().equals("SUCCESS")){
            orderService.paid(payInfo.getOrderNo());
        }
    }
}
