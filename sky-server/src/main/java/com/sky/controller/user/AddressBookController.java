package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "地址簿管理接口")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result addAddressBook(@RequestBody AddressBook addressBook) {
        log.info("新增地址:{}", addressBook);

        addressBookService.addAddressBook(addressBook);

        return Result.success();
    }

    /**
     * 查询地址列表
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询地址列表")
    public Result<List<AddressBook>> list() {
        Long userId= BaseContext.getCurrentId();
        log.info("根据用户id查询所有地址:{}", userId);

        List<AddressBook> list= addressBookService.queryByUserId(userId);

        return Result.success(list);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址:{}", addressBook);

        addressBookService.setDefault(addressBook);

        return Result.success();
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefault() {
        Long userId= BaseContext.getCurrentId();
        log.info("根据用户id查询默认地址:{}", userId);

        AddressBook addressBook= addressBookService.queryDefault(userId);

        return Result.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getDefault(@PathVariable Long id) {

        log.info("根据id查询地址:{}", id);

        AddressBook addressBook= addressBookService.queryById(id);

        return Result.success(addressBook);
    }

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */

    @PutMapping()
    @ApiOperation("修改地址")
    public Result update(@RequestBody AddressBook addressBook) {

        log.info("根据修改地址:{}", addressBook);

        addressBookService.update(addressBook);

        return Result.success();
    }


    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    @DeleteMapping()
    @ApiOperation("根据id删除地址")
    public Result delete(Long id) {

        log.info("根据id删除地址:{}", id);

        addressBookService.deleteById(id);

        return Result.success();
    }
}
