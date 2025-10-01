package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     *  添加地址
     * @param addressBook
     */
    @Override
    public void addAddressBook(AddressBook addressBook) {
        //设置用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        //设置非默认地址
        addressBook.setIsDefault(StatusConstant.DISABLE);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据用户id查询地址
     * @param userId
     * @return
     */
    @Override
    public List<AddressBook> queryByUserId(Long userId) {
        AddressBook addressBook=AddressBook.builder()
                .userId(userId)
                .build();
        return addressBookMapper.select(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void setDefault(AddressBook addressBook) {
        //设置默认地址
        addressBook.setIsDefault(StatusConstant.ENABLE);
        addressBookMapper.updateById(addressBook);
        //设置该用户其它地址为非默认
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(StatusConstant.DISABLE);
        addressBookMapper.setNoDefault(addressBook);

    }

    /**
     * 查询默认地址根据用户id
     * @param userId
     * @return
     */
    @Override
    public AddressBook queryDefault(Long userId) {
        AddressBook addressBook=AddressBook.builder()
                .userId(userId)
                .isDefault(StatusConstant.ENABLE)
                .build();

        return addressBookMapper.select(addressBook).get(0);

    }

    /**
     * 根据id查地址
     * @param id
     * @return
     */
    @Override
    public AddressBook queryById(Long id) {
        AddressBook addressBook=AddressBook.builder()
                .id(id)
                .build();

        return addressBookMapper.select(addressBook).get(0);
    }

    /**
     * 更新地址
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.updateById(addressBook);
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }
}
