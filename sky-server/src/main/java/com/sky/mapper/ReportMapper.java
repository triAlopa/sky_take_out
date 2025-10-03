package com.sky.mapper;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {


    /**
     * 营业额查询
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 用户注册查询，总数查询
     * @param map
     * @return
     */
    Integer userTotalByMap(Map map);

    /**
     * 订单总数，有效数查询
     * @param map
     * @return
     */
    Integer orderCountByMap(Map map);


    /**
     * 热销top10查询
     * @param map
     * @return
     */
    List<GoodsSalesDTO> salesTop10ByMap(Map map);
}
