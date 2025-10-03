package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;


    /**
     * 查询营业额报表 根据时间
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO queryTurnoverByDate(LocalDate begin, LocalDate end) {
        //不能用sql去查，循环遍历加，考虑到可能没有营业额的情况  得到时间范围内每一天
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //计算指定时间范围内
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        //查询营业额 遍历时间列表
        for (LocalDate date : dateList) {
            //得到每一天的起始时间 和最接近的结束时间 转为localdateime 查询数据库
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //转为map集合查询
            Map map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            //查询已完成的订单营业
            map.put("status", Orders.COMPLETED);
            Double turnover = reportMapper.sumByMap(map);
            turnover = turnover == null ? 0 : turnover;
            turnoverList.add(turnover);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtil.join(",", dateList))
                .turnoverList(StringUtil.join(",", turnoverList))
                .build();
    }

    /**
     * 查询用户统计报表
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO queryUserByDate(LocalDate begin, LocalDate end) {
        //不能用sql去查，循环遍历加，考虑到可能没有用户注册的情况  得到时间范围内每一天
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //计算指定时间范围内
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //用户总量列表
        List totalUserList = new ArrayList<>();
        //当日用户新增列表
        List newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //得到每一天的起始时间 和最接近的结束时间 转为localdateime 查询数据库
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //转为map集合查询
            Map map = new HashMap<>();
            map.put("endTime", endTime);
            //查询迄今为止的用户总量
            Integer totalUser = reportMapper.userTotalByMap(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);
            //查询当日新增用户
            map.put("beginTime", beginTime);
            Integer newUser = reportMapper.userTotalByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtil.join(",", dateList))
                .totalUserList(StringUtil.join(",", totalUserList))
                .newUserList(StringUtil.join(",", newUserList))
                .build();
    }

    /**
     * 查询订单统计报表 根据时间
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO queryOrderByDate(LocalDate begin, LocalDate end) {
        //不能用sql去查，循环遍历加，考虑到可能没有订单的情况  得到时间范围内每一天
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //计算指定时间范围内
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //每日订单总数列表
        List<Integer> orderCountList = new ArrayList<>();
        //每日有效订单数列表
        List<Integer> validCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            //得到每一天的起始时间 和最接近的结束时间 转为localdateime 查询数据库
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //转为map集合查询
            Map map = new HashMap<>();
            map.put("endTime", endTime);
            map.put("beginTime", beginTime);
            //查询 今日的订单数，有效订单数
            Integer orderCount = reportMapper.orderCountByMap(map);
            orderCount = orderCount == null ? 0 : orderCount;
            orderCountList.add(orderCount);

            map.put("status", Orders.COMPLETED);
            Integer validCount = reportMapper.orderCountByMap(map);
            validCount = validCount == null ? 0 : validCount;
            validCountList.add(validCount);
        }

        //计算日期范围内订单总数和有效订单数
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        //有效率
        double orderCompletionRate = 0;
        if (orderCountList != null && validCountList != null) {
            totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
            validOrderCount = validCountList.stream().reduce(Integer::sum).get();
            if (validOrderCount != 0) {
                orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
            }
        }

        return OrderReportVO.builder()
                .dateList(StringUtil.join(",", dateList))
                .orderCountList(StringUtil.join(",", orderCountList))
                .validOrderCountList(StringUtil.join(",", validCountList))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .build();
    }

    /**
     * 查询销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO querySaleTop10ByDate(LocalDate begin, LocalDate end) {

        Map map = new HashMap<>();
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("status", Orders.COMPLETED);
        List<GoodsSalesDTO> list = reportMapper.salesTop10ByMap(map);


        //类型转换 转变获得名字集合 和 数量集合 转为字符串
        List<String> names = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        String nameList = StringUtils.join(names, ",");
        String numberList = StringUtils.join(numbers, ",");


        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
