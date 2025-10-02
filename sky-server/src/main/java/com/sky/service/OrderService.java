package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult<OrderVO> queryPage(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 重复下单
     * @param id
     */
    void repetitionOrder(Long id);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO queryByOrderId(Long id);

    /**
     * 订单提醒
     * @param id
     */
    void reminderByOrderId(Long id);

    /**
     * 用户取消订单
     * @param id
     */
    void cancelOrderById(Long id);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO queryAllOrderStatistics();

    /**
     * 管理端查找所有订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult<OrderVO> queryALLPage(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 管理端接单
     * @param ordersConfirmDTO
     * @return
     */
   void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 管理端拒单
     * @param ordersRejectionDTO
     */
    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 管理端取消订单
     * @param ordersCancelDTO
     */
    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    /**
     * 管理端订单派送
     * @param id
     * @return
     */
    void deliveryOrder(Long id);

    /**
     * 完成订单
     * @param id
     */
    void completeOrder(Long id);
}
