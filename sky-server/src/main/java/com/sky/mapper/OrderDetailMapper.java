package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    void insertBatch(List<OrderDetail> orderDetails);

    /**
     * 根据 订单id查询订单详情
     * @param id
     * @return
     */
    @Select("select  * from order_detail where order_id = #{id}")
    List<OrderDetail> selectByOrderId(Long id);
}
