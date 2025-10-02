package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
