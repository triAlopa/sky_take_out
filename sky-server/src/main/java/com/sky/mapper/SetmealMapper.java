package com.sky.mapper;

import com.sky.annotation.autoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 添加菜品
     * @param setmeal
     */
    @Insert("insert into setmeal(category_id, name, price, status, description, image, " +
            "create_time, update_time, create_user, update_user) " +
            "values(#{categoryId}, #{name}, #{price}, #{status}, #{description}, #{image}, " +
            "#{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @autoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    List<SetmealVO> queryPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal queryById(Long id);

    /**
     * 修改套餐
     * @param setmeal
     */
    @autoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);
    /**
     * 根据id查状态
     * @param id
     */
    @Select(" select setmeal.status from setmeal where id=#{id}")
    Integer queryStatus(Integer id);

    /**
     * 根据id批量删除
     * @param ids
     */
    void delByIds(List<Integer> ids);

    /**
     * 根据分类id查看套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where category_id = #{id} and status=1")
    List<Setmeal> selectByCategoryId(Integer id);

    /**
     * 查询套餐总览 起售 停售
     * @param map
     * @return
     */
    Integer queryOverviewSetmealsByMap(Map map);
}
