package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 对单个添加菜品或者套餐
     * @param shoppingCartDTO
     */
    void save(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询当前用户的购物车
     * @param userId
     * @return
     */
    List<ShoppingCart> selectByUserId(Long userId);

    /**
     * 清空购物车
     * @param userId
     */
    void clean(Long userId);

    /**
     *  删除购物车的某一项
     * @param shoppingCartDTO
     */
    void delItem(ShoppingCartDTO shoppingCartDTO);

}
