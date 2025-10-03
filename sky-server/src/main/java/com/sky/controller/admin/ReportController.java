package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "报表统计")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 查询营业额报表 根据时间
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
    @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end) {
        log.info("根据时间 {} ,{}查询营业额统计", begin,end);

        TurnoverReportVO turnoverReportVO = reportService.queryTurnoverByDate(begin,end);

        return Result.success(turnoverReportVO);
    }

    /**
     * 查询用户统计报表 根据时间
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计报表")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end) {
        log.info("根据时间 {} ,{}用户统计", begin,end);

        UserReportVO userReportVO = reportService.queryUserByDate(begin,end);

        return Result.success(userReportVO);
    }

    /**
     * 查询订单统计报表 根据时间
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计报表")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end) {
        log.info("根据时间 {} ,{}订单统计", begin,end);

        OrderReportVO orderReportVO = reportService.queryOrderByDate(begin,end);

        return Result.success(orderReportVO);
    }

    /**
     * 查询销量排名top10接口
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public Result<SalesTop10ReportVO> top10Statistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end) {
        log.info("根据时间 {} ,{}查询销量排名top10", begin,end);

        SalesTop10ReportVO salesTop10ReportVO = reportService.querySaleTop10ByDate(begin,end);

        return Result.success(salesTop10ReportVO);
    }
}
