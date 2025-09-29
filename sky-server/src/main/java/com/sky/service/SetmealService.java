package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐 with 菜品
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     */
    PageResult<SetmealVO> queryPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO queryById(Long id);

    /**
     * 修改套餐 with 菜品
     * @param setmealDTO
     */
    void updateWithDish(SetmealDTO setmealDTO);

    /**
     * 修改套餐状态
     * @param status
     * @param id
     */
    void setStatus(Integer status, Long id);

    /**
     * 遍历删除套餐
     * @param ids
     */
    void delByIds(List<Integer> ids);

    /**
     * 根据分类id查套餐
     * @param id
     * @return
     */
    List<Setmeal> selectByCategoryId(Integer id);
}
