package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private WorkSpaceService workSpaceService;


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

    /**
     * 导出Excel报表接口
     *
     * @param response
     */
    @Override
    public void getBussinessExcel(HttpServletResponse response) {
        //获取统计报表范围日期 查询近30天数据
        LocalDate beginDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);
        //查询近30天数据
        BusinessDataVO businessDataVO = workSpaceService.queryBusinessData(LocalDateTime.of(beginDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX));
        //导出excel 获得类加载器的输入流,获取资源路径 资源根路径
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //创建excel对象
        try {
            XSSFWorkbook excel = new XSSFWorkbook(is);
            //获取模板的标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //写入日期
            sheet.getRow(1).getCell(1).setCellValue("统计报表日期:" + beginDate + "至" + endDate);
            //获取行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());//营业额
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());//完成率
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());//新增用户
            //获取行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());//有效订单
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());//平均客单价

            //批量加入三十天前每一天的
            for (int i = 0; i < 30; i++) {
                //获取行
                row = sheet.getRow(7 + i);
                LocalDate date = beginDate.plusDays(i);
                BusinessDataVO dataVO = workSpaceService.queryBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row.getCell(1).setCellValue(date.toString());//日期
                row.getCell(2).setCellValue(dataVO.getTurnover());//营业额
                row.getCell(3).setCellValue(dataVO.getValidOrderCount());//有效订单
                row.getCell(4).setCellValue(dataVO.getOrderCompletionRate());//完成率
                row.getCell(5).setCellValue(dataVO.getUnitPrice());//平均客单价
                row.getCell(6).setCellValue(dataVO.getNewUsers());//新增用户
            }
            //获取网页端 输出流 写出数据
            ServletOutputStream os = response.getOutputStream();
            //关流
            excel.write(os);
            os.close();
            excel.close();
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
