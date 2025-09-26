package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    @Insert("insert into employee(id, name, username, phone,password ,sex, id_number, status, create_time, update_time) " +
            "values (#{id}, #{name}, #{username}, #{phone},#{password} ,#{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime})")
    void insert(Employee employee);

    List<Employee> queyByName(String name);
}
