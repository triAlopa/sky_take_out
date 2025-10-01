package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 添加地址
     * @param addressBook
     */
    void addAddressBook(AddressBook addressBook);

    /**
     * 根据用户id查询地址
     * @param userId
     * @return
     */
    List<AddressBook> queryByUserId(Long userId);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);

    /**
     * 查询默认地址
     * @param userId
     * @return
     */
    AddressBook queryDefault(Long userId);

    /**
     * 查询id的地址
     * @param id
     * @return
     */
    AddressBook queryById(Long id);

    /**
     * 修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteById(Long id);
}
