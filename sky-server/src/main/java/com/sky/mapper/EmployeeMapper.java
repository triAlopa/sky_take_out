package com.sky.mapper;

import com.sky.annotation.autoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EmployeeMapper {


    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @autoFill(value = OperationType.INSERT)
    @Insert("insert into employee(id, name, username, phone,password ,sex, id_number, status, " +
            "create_time, update_time,create_user,update_user) " +
            "values (#{id}, #{name}, #{username}, #{phone},#{password} ,#{sex}, " +
            "#{idNumber}, #{status}, #{createTime}, #{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);

    /**
     * 根据姓名查询员工
     * @param name
     * @return
     */
    List<Employee> queryByName(String name);

    @autoFill(value = OperationType.UPDATE)
    /**
     * 修改员工状态
     * @param employee
     */
    void update(Employee employee);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Select("select  * from employee where id = #{id}")
    Employee selectById(Long id);
}
