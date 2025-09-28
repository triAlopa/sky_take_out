package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Service
public class DishServiceImpl implements DishService {
    //菜品Mapper
    @Autowired
    private DishMapper dishMapper;
    //菜品口味Mapper
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    //套餐菜品Mapper
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增菜品 with 口味
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish);
        //主键回显
        dishMapper.insert(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (CollectionUtils.isEmpty(flavors)) {
            return;
        }
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dish.getId());
        }

        dishFlavorMapper.insertList(flavors);
    }

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult<DishVO> queryPage(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

//        Dish dish=Dish.builder().categoryId(Long.valueOf(dishPageQueryDTO.getCategoryId()))
//                .name(dishPageQueryDTO.getName())
//                .status(dishPageQueryDTO.getStatus()).build();
//        BeanUtils.copyProperties(dish,dishPageQueryDTO);

        Page<DishVO> query = (Page<DishVO>) dishMapper.query(dishPageQueryDTO);

        return new PageResult<>(query.getTotal(), query.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithFlavor(List<Long> ids) {

        //判断菜品是否在售
        for (Long id : ids) {
            Dish dish = dishMapper.queryById(id);
            if (dish.getStatus() == StatusConstant.ENABLE)
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        //判断是否关联了套餐
        List<Long> longs = setmealDishMapper.queryByDishId(ids);
        if (longs != null && !CollectionUtils.isEmpty(longs)) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        dishMapper.delByIds(ids);

        for (Long id : ids) {
            dishFlavorMapper.delByDishId(id);
        }

    }

    /**
     * 根据id查询菜品 with 口味
     * 修改数据回显
     * @param id
     * @return
     */
    @Override
    public DishVO queryById(Long id) {
        return dishMapper.queryWithFlavorById(id);
    }

    /**
     * 修改菜品 with 口味
     * @param dishDTO
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.update(dish);

        //先对目标id的原有口味删除，再添加
        dishFlavorMapper.delByDishId(dish.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //设置逻辑外键 菜品id
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertList(flavors);
        }
    }

    /**
     * 修改菜品状态
     * @param status
     * @param id
     */
    @Override
    public void setStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status).build();
        dishMapper.update(dish);
    }

    /**
     * 根据菜品分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> queryByCategoryId(Long categoryId) {
        return dishMapper.queryByCategoryId(categoryId);
    }
}
