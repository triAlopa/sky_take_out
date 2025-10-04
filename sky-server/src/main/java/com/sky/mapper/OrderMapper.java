package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 查询订单列表
     * @param ordersPageQueryDTO
     * @return
     * */
    List<OrderVO> select(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查找当前订单
     * @param id
     * @return
     */
    @Select("select  * from orders where id=#{id}")
    Orders selectById(Long id);

    @Select("select  status,count(status) as number from orders group by status;")
    List<Map<String, Object>> getStatusMap();

    /**
     * 查询未支付订单
     * @return
     */
    @Select("select * from orders where pay_status = 0")
    List<Orders> selectAllUnpaid();

    /**
     * 查询还没有完成的订单
     * @return
     */
    @Select("select * from orders where status = 4")
    List<Orders> selectAllDELIVERY_IN_PROGRESS();

    /**
     * 查询时间内中的订单
     * @param status
     * @param time
     * @return
     */
    @Select("select  * from orders where status=#{status} and order_time<#{time}")
    List<Orders> getOrderStatusAndTime(Integer status, LocalDateTime time);

    /**
     * 查询所有订单
     * @return
     */
    @Select("select count(id) from orders")
    Integer queryAllOrders();

}
