package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.WatchService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public BusinessDataVO queryBusinessData(LocalDateTime beginTime, LocalDateTime endTime) {
        //存入map查询
        Map map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        //订单总数
        Integer orderCount = reportMapper.orderCountByMap(map);
        //添加status为5 查询当天有效订单数
        map.put("status", Orders.COMPLETED);
        //有效订单数
        Integer validOrderCount = reportMapper.orderCountByMap(map);
        Double orderCompletionRate = 0.0;
        if (orderCount != 0) orderCompletionRate = validOrderCount.doubleValue() / orderCount.doubleValue();//计算订单完成率
        //营业额
        Double turnover = reportMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;
        //平均客单价
        Double unitPrice = 0.0;
        if (validOrderCount != 0) unitPrice = turnover / validOrderCount;//计算平均客单价
        //新增用户数
        Integer newUsers = reportMapper.userTotalByMap(map);
        newUsers = newUsers == null ? 0 : newUsers;

        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .unitPrice(unitPrice)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderCount)
                .turnover(turnover)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    @Override
    public SetmealOverViewVO queryOverviewSetmeals() {
        //存入map查询
        Map map = new HashMap<>();
        //查询无效套餐 设置查询状态
        map.put("status", StatusConstant.DISABLE);
        //无效套餐 已停售数量
        Integer discontinued = setmealMapper.queryOverviewSetmealsByMap(map);
        //查询有效套餐 设置查询状态
        map.put("status", StatusConstant.ENABLE);
        //已启售数量
        Integer sold = setmealMapper.queryOverviewSetmealsByMap(map);
        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @Override
    public DishOverViewVO queryOverviewDishes() {
        //查询有效的菜品
        Integer status = StatusConstant.ENABLE;
        //已启售数量
        Integer sold = 0;
        sold = dishMapper.queryOverviewDishesByStatus(status);
        status = StatusConstant.DISABLE;
        //已停售数量
        Integer discontinued = 0;
        discontinued = dishMapper.queryOverviewDishesByStatus(status);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @Override
    public OrderOverViewVO queryOverviewOrders() {
        List<Map<String, Object>> statusMap = orderMapper.getStatusMap();
        //待接单数量
        Integer waitingOrders = 0;
        //待派送数量
        Integer deliveredOrders = 0;
        //已完成数量
        Integer completedOrders = 0;
        //已取消数量
        Integer cancelledOrders = 0;
        //全部订单
        Integer allOrders = 0;
        for (Map<String, Object> map : statusMap) {
            //获取 sql查找的数量状态 遍历 因为在sql已经起别名了
            Integer status = (Integer) map.get("status");
            Long number = (Long) map.get("number");
            //强制转换
            Integer num = number.intValue();
            if (status.equals(Orders.TO_BE_CONFIRMED)) waitingOrders = num;//待接单
            else if (status.equals(Orders.CONFIRMED)) deliveredOrders = num;//等待派送
            else if (status.equals(Orders.COMPLETED)) completedOrders = num;//已完成
            else if (status.equals(Orders.CANCELLED)) cancelledOrders = num;//已取消
        }
        allOrders = orderMapper.queryAllOrders();

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }
}
