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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@Api(tags = "菜品管理")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

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

        //判断添加了某一个菜品，则清楚 在redis下这个key的数据，保证用户端和管理端的一致性
        String key="dish_"+dishDTO.getCategoryId();
        redisTemplate.delete(key);

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
        //清楚所有菜品缓存数据
        clearCache("dish_*");

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
        //清楚所有菜品缓存数据
        clearCache("dish_*");

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
        //清楚所有菜品缓存数据
        clearCache("dish_*");

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

    //清楚 redis缓存里面的所有菜品数据
    private void clearCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
