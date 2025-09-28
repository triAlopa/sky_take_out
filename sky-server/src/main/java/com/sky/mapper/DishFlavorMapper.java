package com.sky.mapper;

import com.sky.annotation.autoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量添加口味
     * @param flavors
     */
    void insertList(List<DishFlavor> flavors);

    /**
     * 根据菜品id查询状态
     * @param id
     * @return
     */
    @Select("select status from dish where id = #{id}")
    Integer queryStatus(Long id);

    /**
     * 根据菜品id删除口味
     * @param id
     */
    @Delete(" delete from dish_flavor where dish_id=#{id}")
    void delByDishId(Long id);
}
