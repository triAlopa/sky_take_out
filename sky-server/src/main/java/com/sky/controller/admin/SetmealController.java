package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("adminSetmealController")
@Api(tags = "套餐管理")
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @ApiOperation("新增套餐")
    @PostMapping
    @Cacheable(cacheNames = "setmealCache" ,key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐:{}", setmealDTO);

        setmealService.saveWithDish(setmealDTO);

        return Result.success();

    }

    /**
     * 分页查询套餐
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation("分页查询套餐")
    @GetMapping("/page")
    public Result<PageResult<SetmealVO>> stopSale(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐:{}", setmealPageQueryDTO);

        PageResult<SetmealVO> pageResult = setmealService.queryPage(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> queryById(@PathVariable Long id) {
        log.info("根据id查询套餐:{}", id);

        SetmealVO setmealVO = setmealService.queryById(id);

        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache" ,allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐:{}", setmealDTO);

        setmealService.updateWithDish(setmealDTO);

        return Result.success();
    }

    /**
     *  修改套餐状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改套餐状态")
    @CacheEvict(cacheNames = "setmealCache" ,allEntries = true)
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("修改套餐状态:{} ,{}", status, id);

        setmealService.setStatus(status, id);

        return Result.success();
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping()
    @ApiOperation("删除套餐")
    @CacheEvict(cacheNames = "setmealCache" ,allEntries = true)
    public Result delete(@RequestParam("ids") List<Integer> ids) {
        log.info("删除套餐:{}", ids);

        setmealService.delByIds(ids);

        return Result.success();
    }
}
