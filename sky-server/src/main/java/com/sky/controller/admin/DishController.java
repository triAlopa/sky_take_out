package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "菜品管理")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品: {}", dishDTO);

        dishService.saveWithFlavor(dishDTO);

        return Result.success();
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult<DishVO>> queryPage(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品: {}", dishPageQueryDTO);

        PageResult<DishVO> pageResult = dishService.queryPage(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品: {}", ids);

        dishService.deleteWithFlavor(ids);

        return Result.success();
    }
    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> queryById(@PathVariable Long id) {
        log.info("根据id查菜品； {}", id);

        DishVO DishVO = dishService.queryById(id);

        return Result.success(DishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品: {}", dishDTO);

        dishService.updateWithFlavor(dishDTO);

        return Result.success();
    }

    /**
     * 修改菜品状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品状态")
    public Result setStatus(@PathVariable Integer status, @RequestParam Long id) {
        log.info("修改菜品状态: {},{}", status, id);

        dishService.setStatus(status, id);

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> queryByCategoryId(@RequestParam Long categoryId) {
        log.info("根据分类id查询菜品: {}", categoryId);

        List<Dish> dishList = dishService.queryByCategoryId(categoryId);

        return Result.success(dishList);
    }
}
