package com.sky.mapper;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    List<Category> queryPage(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 添加分类
     * @param category
     */
    @Insert("insert into category(id, type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{id},#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insertCategory(Category category);

    /**
     * 根据id修改分类
     * @param category
     */
    void update(Category category);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id=#{id}")
    void delById(Long id);

    /**
     * 有效分类 查询 根据类型
     * @param type
     * @return
     */
    List<Category> list(Integer type);
}
