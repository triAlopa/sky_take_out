package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 用户添加 菜品或者套餐
     *
     * @param shoppingCartDTO
     */
    @Override
    public void save(ShoppingCartDTO shoppingCartDTO) {
        //获取当前用户id
        Long UserId = BaseContext.getCurrentId();
        //判断当前加入到购物车中的商品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(UserId);

        List<ShoppingCart> list = shoppingCartMapper.queryDishOrSetmealId(shoppingCart);
        //如果是存在的，则更新数量加1
        if (list != null && list.size() > 0) {
            shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberByUserId(shoppingCart);
            return;
        }
        //如果不存在，则新增数据
        //判断添加的是菜品还是套餐
        if (shoppingCart.getDishId() != null) {
            //添加的是菜品
            Dish dish = dishMapper.queryById(shoppingCart.getDishId());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setName(dish.getName());
        } else {
            //添加的是套餐
            Setmeal setmeal = setmealMapper.queryById(shoppingCart.getSetmealId());
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setName(setmeal.getName());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());

        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 根据用户查找他的购物车
     * @param userId
     * @return
     */
    @Override
    public List<ShoppingCart> selectByUserId(Long userId) {
        return shoppingCartMapper.selectByUserId(userId);
    }

    /**
     * 清空购物车
     * @param userId
     */
    @Override
    public void clean(Long userId) {
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车中的菜品或套餐
     *
     * @param shoppingCartDTO
     */
    @Override
    public void delItem(ShoppingCartDTO shoppingCartDTO) {
        //赋值
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //获取用户id并赋值
        Long UserId = BaseContext.getCurrentId();
        shoppingCart.setUserId(UserId);
        //根据条件查询数据库
        List<ShoppingCart> list = shoppingCartMapper.queryDishOrSetmealId(shoppingCart);
        if (list != null && list.size() > 0) {
            ShoppingCart shoppingCartRS = list.get(0);
            //判断数量是否为1 不为1则减去1个单位，为1则删除
            Integer number = shoppingCartRS.getNumber();
            //大于1更新操作
            if (number > 1) {
                shoppingCartRS.setNumber(number - 1);
                shoppingCartMapper.updateNumberByUserId(shoppingCartRS);
            } else if (number == 1) {//等于1 删除操作
                //xml 要动态判断是删除菜品还是套餐
                shoppingCartMapper.deleteByUserIdAndDishIdOrSetmealId(shoppingCartRS);
            } else {
                throw new RuntimeException(MessageConstant.UNKNOWN_ERROR);
            }
        }
    }
}
