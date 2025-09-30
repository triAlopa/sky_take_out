package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "用户套餐接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

    /**
     * 根据分类id查套餐
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查套餐")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")
    public Result<List> list(Integer categoryId) {
        log.info("根据分类id查套餐；{}", categoryId);

        List<Setmeal> list= setmealService.selectByCategoryId(categoryId);

        return Result.success(list);
    }

    /**
     * 根据套餐id查套餐里的菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查套餐里的菜品")
    public Result<List> queryWithDish(@PathVariable Long id) {
        log.info("根据套餐id查套餐里的菜品；{}", id);

        List<DishItemVO> list= dishService.queryBySetmealId(id);

        return Result.success(list);
    }

}
