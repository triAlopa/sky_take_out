package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 根据用户id和菜品id或套餐id查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> queryDishOrSetmealId(ShoppingCart shoppingCart);

    /**
     *  更新菜品或套餐数量
     * @param shoppingCart
     */
    void updateNumberByUserId(ShoppingCart shoppingCart);

    /**
     * 加入购物车的一项数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id查询购物车
     * @param userId
     * @return
     */
    @Select("select * from shopping_cart where user_id=#{userId}")
    List<ShoppingCart> selectByUserId(Long userId);

    /**
     * 根据用户id删除购物车
     * @param userId
     */
    @Delete("delete  from shopping_cart where user_id=#{userId}")
    void deleteByUserId(Long userId);

    /**
     * 删除购物车的一项
     * @param shoppingCartRS
     */
    void deleteByUserIdAndDishIdOrSetmealId(ShoppingCart shoppingCartRS);
}
