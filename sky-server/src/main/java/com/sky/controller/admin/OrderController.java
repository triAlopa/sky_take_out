package com.sky.controller.admin;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 各个状态的订单数量统计
     *
     * @param
     * @return
     */
    @ApiOperation("各个状态的订单数量统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getStatistics() {
        log.info("各个状态的订单数量统计");

        OrderStatisticsVO orderStatisticsVO = orderService.queryAllOrderStatistics();

        return Result.success(orderStatisticsVO);
    }

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult<OrderVO>> searchOrder(OrdersPageQueryDTO ordersPageQueryDTO) {

        log.info("订单搜索：{}", ordersPageQueryDTO);

        PageResult<OrderVO> queryPage = orderService.queryALLPage(ordersPageQueryDTO);

        return Result.success(queryPage);
    }


    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @ApiOperation("查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> orderDetail(@PathVariable Long id) {
        log.info("查询订单详情，订单id：{}", id);

        OrderVO orderVO = orderService.queryByOrderId(id);

        return Result.success(orderVO);
    }


    /**
     * 管理端接单
     *
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("管理端接单")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("管理端接单：{}", ordersConfirmDTO);

       orderService.confirmOrder(ordersConfirmDTO);

        return Result.success();
    }

    /**
     * 管理端拒单
     *
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("管理端拒单")
    public Result  rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("管理端拒单：{}", ordersRejectionDTO);

        orderService.rejectionOrder(ordersRejectionDTO);

        return Result.success();
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("管理端取消订单：{}", ordersCancelDTO);

        orderService.cancelOrder(ordersCancelDTO);

        return Result.success();
    }


    /**
     * 派送订单
     *
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<PageResult> deliveryOrder(@PathVariable Long id) {
        log.info("派送订单：{}", id);

        orderService.deliveryOrder(id);

        return Result.success();
    }

    /**
     * 完成订单
     *
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<PageResult> completeOrder(@PathVariable Long id) {
        log.info("完成订单：{}", id);

        orderService.completeOrder(id);

        return Result.success();
    }

}
