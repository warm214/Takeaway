package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务类
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ?")//每分钟出发一次
//    @Scheduled(cron = "0/7 * * * * ?")
    public void processTimeoutOrder(){
        log.info("定时处理超市任务{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if (ordersList!=null&& ordersList.size()>0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }

        }

    }


    /**
     * 处理一致处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点出发一次
//    @Scheduled(cron = "0/5 * * * * ?")
    public void processDeliveryOrder(){
        log.info("处理一致处于派送中的订单{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if (ordersList!=null&& ordersList.size()>0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orders.setCancelReason("订单超时自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
}





















