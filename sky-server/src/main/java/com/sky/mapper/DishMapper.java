package com.sky.mapper;

import com.sky.annotation.autoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品 with 口味
     * @param dish
     */
    @autoFill(value = OperationType.INSERT)
    @Insert("insert into dish(name, category_id, price, image, description, status, " +
            "create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, " +
            "#{createTime}, #{updateTime}, #{createUser}, #{updateUser})" )
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(Dish dish);

    /**
     * 查询菜品
     * @return
     */
    List<DishVO> query(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */

    void delByIds(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish queryById(Long id);

    /**
     * 更新菜品
     * @param dish
     */
    @autoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据id查询菜品 with 口味
     * @param id
     * @return
     */
    DishVO queryWithFlavorById(Long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where  dish.category_id=#{categoryId} and status=1")
    List<Dish> queryByCategoryId(Long categoryId);

    /**
     * 根据分类id查询菜品 with 口味
     * @param categoryId
     * @return
     */
    List<DishVO> listWithFlavor(Long categoryId);



}
