package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class myTask {
    @Autowired
    private OrderMapper orderMapper;

    //@Scheduled(cron = "0 * * * * ?") TODO
    public void executedPayStatus() {
        log.info("在{}检测订单支付状态任务执行了:", new Date());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
       /* // 检测订单支付状态
        List<Orders> list = orderMapper.selectAllUnpaid();
        list.forEach(order -> {
            //获得下单分钟
            int orderMinute = order.getOrderTime().getMinute();
            int nowMinute = LocalDateTime.now().getMinute();
            if (nowMinute - orderMinute >= 15) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason("超时未支付，订单自动取消");
                orderMapper.update(order);
            }
        });*/
        List<Orders> list = orderMapper.getOrderStatusAndTime(1, time);
        list.forEach(order -> {
            order.setStatus(Orders.CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            order.setCancelReason("超时未支付，订单自动取消");
            orderMapper.update(order);
        });
    }

    /**
     * 处理完成的订单，一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void executedOrder() {
        log.info("在{}检测订单完成状态任务执行", new Date());

        LocalDateTime time = LocalDateTime.now().plusHours(-1);
      /*  List<Orders> list = orderMapper.selectAllDELIVERY_IN_PROGRESS();

        list.forEach(order -> {
            //如果处于派送中
            if (order.getStatus() == Orders.DELIVERY_IN_PROGRESS) {
                order.setStatus(Orders.COMPLETED);
                order.setCheckoutTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        });*/

        List<Orders> list = orderMapper.getOrderStatusAndTime(Orders.DELIVERY_IN_PROGRESS, time);
        list.forEach(order -> {
            order.setStatus(Orders.COMPLETED);
            order.setCheckoutTime(LocalDateTime.now());
            orderMapper.update(order);
        });
    }

}
