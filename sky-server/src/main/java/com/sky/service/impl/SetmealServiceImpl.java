package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 添加套餐
     *
     * @param setmealDTO
     */
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        //赋值给 数据库对应的实体类
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //状态为开售
        setmeal.setStatus(StatusConstant.ENABLE);
        //添加 套餐主体
        setmealMapper.insert(setmeal);
        //添加 套餐菜品 考虑菜品的附属套餐id 采用主键id
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        //遍历赋值
        dishes.forEach(dish -> {
            dish.setSetmealId(setmeal.getId());
        });

        //批量添加
        setmealDishMapper.forInsert(dishes);
    }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     */
    @Override
    public PageResult<SetmealVO> queryPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        //分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        //根据参数查询
        Page<SetmealVO> page = (Page<SetmealVO>) setmealMapper.queryPage(setmealPageQueryDTO);

        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     *  根据id 查询套餐 with 套餐下的菜品
     * @param id
     * @return
     */
    @Override
    public SetmealVO queryById(Long id) {
        //查询套餐
        Setmeal setmeal = setmealMapper.queryById(id);

        //封装为 网页端观看的视图对象
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        //查询套餐下的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.queryBySetmealId(id);
        //赋值给视图对象
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateWithDish(SetmealDTO setmealDTO) {
        //赋值给 数据库对应的实体类
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //修改 套餐主体
        setmealMapper.update(setmeal);
        //删除 套餐菜品
        setmealDishMapper.delBySetmealId(setmeal.getId());
        //再对套餐当前下的菜品进行 套餐id赋值，再批量添加
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if (dishes != null && dishes.size() > 0) {
            dishes.forEach(dish -> {
                dish.setSetmealId(setmeal.getId());
            });
            setmealDishMapper.forInsert(dishes);
        }
    }

    /**
     * 修改套餐状态
     * @param status
     * @param id
     */
    @Override
    public void setStatus(Integer status, Long id) {
        //判断是否为未启用状态
        if (status == StatusConstant.DISABLE) {
            //查询套餐下的菜品
            List<SetmealDish> setmealDishes = setmealDishMapper.queryBySetmealId(id);
            //判断是否有菜品
            if (setmealDishes != null && setmealDishes.size() > 0) {
                //便利得到菜品的id，判断是否为启用状态
                setmealDishes.forEach(setmealDish -> {
                    Long dishId = setmealDish.getDishId();
                    Dish dish = dishMapper.queryById(dishId);
                    if (dish.getStatus() == StatusConstant.DISABLE) {
                        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        //修改状态
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delByIds(List<Integer> ids) {
        //判断是否为空
        if (ids != null && ids.size() > 0) {
            //遍历查询状态  是否为启用，启用不许删除
            ids.forEach(id -> {
                Integer status = setmealMapper.queryStatus(id);
                if (status == StatusConstant.ENABLE) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
                }
            });
            setmealMapper.delByIds(ids);
            setmealDishMapper.delBySetmealIds(ids);
        } else {
            throw new DeletionNotAllowedException("请选择删除的套餐");
        }


    }
}
