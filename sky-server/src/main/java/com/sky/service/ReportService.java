package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    /**
     * 查询营业额报表 根据时间
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO queryTurnoverByDate(LocalDate begin, LocalDate end);

    /**
     * 查询用户统计报表
     * @param begin
     * @param end
     * @return
     */
    UserReportVO queryUserByDate(LocalDate begin, LocalDate end);

    /**
     * 查询订单统计报表 根据时间
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO queryOrderByDate(LocalDate begin, LocalDate end);

    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO querySaleTop10ByDate(LocalDate begin, LocalDate end);

    /**
     * 导出Excel报表接口
     * @param response
     */
    void getBussinessExcel(HttpServletResponse response);
}
