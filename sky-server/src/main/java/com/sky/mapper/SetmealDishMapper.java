package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id 查询是否关联套餐
     * @param ids
     * @return
     */
    List<Long> queryByDishId(List<Long> ids);


    /**
     *  批量为套餐添加菜品
     * @param dishes
     */
    void forInsert(List<SetmealDish> dishes);

    /**
     * 根据套餐id 查询菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> queryBySetmealId(Long id);

    /**
     * 根据套餐id 删除套餐附属下的菜品
     * @param setmealId
     */
    @Delete("delete  from setmeal_dish where setmeal_id=#{setmealId} ")
    void delBySetmealId(Long setmealId);

    /**
     * 根据套餐id批量删除套餐
     * @param ids
     */
    void delBySetmealIds(List<Integer> ids);
}
