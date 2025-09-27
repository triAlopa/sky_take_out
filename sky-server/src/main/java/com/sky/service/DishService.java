package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 保存菜品
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult<DishVO> queryPage(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品 包含口味
     * @param ids
     */
    void deleteWithFlavor(List<Long> ids);
}
