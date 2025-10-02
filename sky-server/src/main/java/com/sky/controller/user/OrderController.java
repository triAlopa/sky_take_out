package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "用户端订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("用户端下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户端下单，订单信息：{}", ordersSubmitDTO);

        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);

        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("历史订单")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("历史订单查询：{}", ordersPageQueryDTO);

        PageResult<OrderVO> list = orderService.queryPage(ordersPageQueryDTO);

        return Result.success(list);
    }

    /**
     * 再来一单
     */
    @PostMapping("repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        log.info("再来一单，订单id：{}", id);

        orderService.repetitionOrder(id);

        return Result.success();
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @ApiOperation("查询订单详情")
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> orderDetail(@PathVariable Long id) {
        log.info("查询订单详情，订单id：{}", id);

        OrderVO orderVO = orderService.queryByOrderId(id);

        return Result.success(orderVO);
    }

    @ApiOperation("用户催单")
    @GetMapping("reminder/{id}")
    public Result reminder(@PathVariable Long id) {
        log.info("用户催单，订单id：{}", id);

        orderService.reminderByOrderId(id);

        return Result.success();
    }

    /**
     * 用户取消订单
     * @param id
     * @return
     */
    @ApiOperation("用户取消订单")
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id) {
        log.info("用户取消订单，订单id：{}", id);

        orderService.cancelOrderById(id);

        return Result.success();
    }
}
