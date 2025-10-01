package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 插入一条数据
     * @param addressBook
     */
    @Insert(("insert into address_book(user_id, consignee, sex, phone, province_code, " +
            "province_name, city_code, city_name, district_code, district_name, detail, label, is_default)" +
            "values (#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, " +
            "#{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})"))
    void insert(AddressBook addressBook);

    /**
     * 根据条件查找地址
     * @param addressBook
     * @return
     */
//    @Select("select  * from address_book where user_id=#{userId}")
    List<AddressBook> select(AddressBook addressBook);

//    /**
//     * 修改默认地址
//     * @param addressBook
//     */
//    @Update("update address_book set is_default=1 where id=#{id}")
//    void setDefaultAddress(AddressBook addressBook);

    /**
     * 修改默认地址
     * @param addressBook
     */
    void updateById(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("delete  from address_book where id=#{id}")
    void deleteById(Long id);

    /**
     * 修改该用户其它地址为非默认
     * @param addressBook
     */
    @Update("update address_book set is_default=0 where user_id=#{userId} and id !=#{id}")
    void setNoDefault(AddressBook addressBook);
}
