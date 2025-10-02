package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    /**
     * 添加订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //业务异常逻辑处理
        //0.1下单地址是否为空
        AddressBook addressBook = AddressBook.builder()
                .id(ordersSubmitDTO.getAddressBookId())
                .build();
        List<AddressBook> addressBooks = addressBookMapper.select(addressBook);
        if (addressBooks == null || addressBooks.size() == 0) {
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //0.2下单购物车是否为空
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        //得到用户当前购物车
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByUserId(userId);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            //抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //赋值
        Orders orders = new Orders();
        //属性拷贝
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        //当前下单时间
        orders.setOrderTime(LocalDateTime.now());
        //用户id
        orders.setUserId(BaseContext.getCurrentId());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        AddressBook address = addressBooks.get(0);
        orders.setAddress(address.getProvinceName() + address.getCityName() + address.getDistrictName() + address.getDetail());
        orders.setUserName(String.valueOf(userId));
        orders.setUserId(userId);
        orders.setPhone(addressBooks.get(0).getPhone());
        orders.setConsignee(addressBooks.get(0).getConsignee());

        orderMapper.insert(orders);

        //添加订单详细菜品
        List<OrderDetail> list = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            list.add(orderDetail);
        }
        //批量添加
        orderDetailMapper.insertBatch(list);

        //清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();

        return orderSubmitVO;

    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

     /*   //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );*/

        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 订单分页查询 用户端本身
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult<OrderVO> queryPage(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<OrderVO> orders = (Page<OrderVO>) orderMapper.select(ordersPageQueryDTO);

        return new PageResult<>(orders.getTotal(), orders.getResult());

    }

    /**
     * 再来一单 直接下单的那种 待支付
     *
     * @param id
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void repetitionOrder(Long id) {
        //根据id查找这个id对应的主体订单
        Orders order = orderMapper.selectById(id);
        //校验是否存在订单
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //校验是否存在地址
        if (order.getAddress() == null || order.getAddress().equals("")) {
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //获取当前的旧订单id为查找附属菜品或者套餐
        Long orderOldId = order.getId();
        //将当前的id修改为null
        order.setId(null);
        //购买订单状态
        order.setStatus(Orders.PENDING_PAYMENT);
        //支付状态
        order.setPayStatus(Orders.UN_PAID);
        //修改订单的创建时间
        order.setOrderTime(LocalDateTime.now());
        //结账时间
        order.setCheckoutTime(null);
        //送达时间
        order.setDeliveryTime(null);
        //预计送达时间 一小时后
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1l));
        //修改订单号
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        orderMapper.insert(order);
        //id数据回显 就修改订单下的详情套餐或者菜品id
        Long orderNewId = order.getId();
        //根据 orderOldId 查找订单详情
        List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(orderOldId);
        //购物车数据为空
        if (orderDetails == null || orderDetails.size() == 0) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrderId(orderNewId);
            //查询出来的id有值 赋值为null
            orderDetail.setId(null);
        }
        //批量添加
        orderDetailMapper.insertBatch(orderDetails);

    }

    /**
     * 查询某个订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO queryByOrderId(Long id) {
        Orders orders = orderMapper.selectById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Long ordersId = orders.getId();
        List<OrderDetail> details = orderDetailMapper.selectByOrderId(ordersId);
        OrderVO orderVO = new OrderVO();
        if (details != null && details.size() > 0) {
            orderVO.setOrderDetailList(details);
            String dishNames = getOrderDishName(details);
            orderVO.setOrderDishes(dishNames);
        }
        BeanUtils.copyProperties(orders, orderVO);

        return orderVO;
    }

    @Override
    public void reminderByOrderId(Long id) {
        //根据id查找这个id对应的主体订单
        Orders order = orderMapper.selectById(id);
        //校验是否存在订单
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //校验订单状态 用户是否付款
        if (order.getStatus() == Orders.PENDING_PAYMENT || order.getPayStatus() == Orders.UN_PAID) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //TODO 发送对商家提醒

    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    @Override
    public void cancelOrderById(Long id) {
        Orders orders = orderMapper.selectById(id);
        //状态>2就不可以取消订单 接单以后不可以取消
        if (orders == null || orders.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //设置为取消订单
        orders.setStatus(Orders.CANCELLED);
        if (orders.getPayStatus() == Orders.PAID) {
            //如果已经支付了 那么就退款
            orders.setPayStatus(Orders.REFUND);
        }
        //只是展示给顾客看多少钱，不做修改
//        //实收金额为0
//        orders.setAmount(BigDecimal.valueOf(0));
        //取消订单时间
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason("用户取消");
        //修改订单
        orderMapper.update(orders);
    }


    @Override
    public OrderStatisticsVO queryAllOrderStatistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();

        //
        List<Map<String, Object>> data = orderMapper.getStatusMap();

        for (Map<String, Object> datum : data) {
            //获取 sql查找的数量状态 遍历 因为在sql已经起别名了
            Integer status = (Integer) datum.get("status");
            Long number = (Long) datum.get("number");
            //强制转换
            Integer num = number.intValue();
            //待派送
            if (status == Orders.CONFIRMED) {
                orderStatisticsVO.setConfirmed(num);
            }
            //派送中
            else if (status == Orders.DELIVERY_IN_PROGRESS) {
                orderStatisticsVO.setDeliveryInProgress(num);
            }
            //待接单
            else if (status == Orders.TO_BE_CONFIRMED) {
                orderStatisticsVO.setToBeConfirmed(num);
            }
        }

        return orderStatisticsVO;
    }

    /**
     * 管理端查找所有订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult<OrderVO> queryALLPage(OrdersPageQueryDTO ordersPageQueryDTO) {

        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<OrderVO> orders = (Page<OrderVO>) orderMapper.select(ordersPageQueryDTO);
        //添加菜品信息字段
        orders.forEach(order -> {
            String dishNames = getOrderDishName(order.getOrderDetailList());
            order.setOrderDishes(dishNames);
        });


        return new PageResult<>(orders.getTotal(), orders.getResult());
    }

    //遍历拼接菜品或者套餐名
    private String getOrderDishName(List<OrderDetail> orderDetailList) {
        List<String> dishNames = orderDetailList.stream().map(orderDetail -> {
            String dishName = orderDetail.getName() + "*" + orderDetail.getNumber() + ";";
            return dishName;
        }).collect(Collectors.toList());
        //将菜名拼接一起
        return String.join("", dishNames);
    }

    /**
     * 管理端接单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        //查找订单
        Orders orders = orderMapper.selectById(ordersConfirmDTO.getId());
        //更新状态
        orders.setStatus(Orders.CONFIRMED);
//        //立即送出
//        orders.setDeliveryStatus(1);

        orderMapper.update(orders);
    }

    /**
     * 管理端拒单
     *
     * @param ordersRejectionDTO
     */
    @Override
    public void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        //查找订单
        Orders orders = orderMapper.selectById(ordersRejectionDTO.getId());
        //只有待接单才可以拒绝
        if (orders == null || !orders.getOrderTime().equals(Orders.REFUND)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //更新支付状态
        orders.setPayStatus(Orders.REFUND);
        //更新状态 取消
        orders.setStatus(Orders.CANCELLED);
        //设置拒绝原因
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        //取消时间
        orders.setCancelTime(LocalDateTime.now());
//        //预计时间
//        orders.setDeliveryTime(null);
        //配送状态
        orders.setDeliveryStatus(null);

        orderMapper.update(orders);
    }

    /**
     * 管理端取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        //查找订单
        Orders orders = orderMapper.selectById(ordersCancelDTO.getId());
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        //设置为取消订单
        orders.setStatus(Orders.CANCELLED);
        //取消订单时间
        orders.setCancelTime(LocalDateTime.now());
        if (orders.getPayStatus() == Orders.PAID) {
            //如果已经支付了 那么就退款
            orders.setPayStatus(Orders.REFUND);
        }

        orderMapper.update(orders);
    }

    /**
     * 管理端派送订单
     *
     * @param id
     */
    @Override
    public void deliveryOrder(Long id) {
        //查找订单
        Orders orders = orderMapper.selectById(id);
        //没有支付不可以派送
        if (orders == null || orders.getStatus() != Orders.PAID) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //没有接单不可以完成
        if (!orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //更新状态
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        //派送时间
        orders.setDeliveryTime(LocalDateTime.now());
        //立即送出
        orders.setDeliveryStatus(1);

        orderMapper.update(orders);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    @Override
    public void completeOrder(Long id) {
        //查找订单
        Orders orders = orderMapper.selectById(id);
        //没有派送不可以完成
        if (orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //更新状态
        orders.setStatus(Orders.COMPLETED);
        //设置送达时间
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }


}
