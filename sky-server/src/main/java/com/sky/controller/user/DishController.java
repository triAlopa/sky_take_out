package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/user/dish")
@RestController("userDishController")
@Slf4j
@Api(tags = "用户端菜品接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查看菜品")
    public Result list(Long categoryId) {
        log.info("根据分类id查看菜品: {}", categoryId);

        List<DishVO> dishes = dishService.listWithFlavor(categoryId);

        return Result.success(dishes);
    }


}
