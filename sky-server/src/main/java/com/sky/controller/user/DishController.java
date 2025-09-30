package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查看菜品")
    public Result list(Long categoryId) {
        //在redis里面根据key来查找是否有对应缓存数据 key规则:dish_d
        log.info("根据分类id查看菜品: {}", categoryId);
        String key = "dish_" + categoryId;

        //向redis发出菜品查找请求
        List<DishVO> dishes = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //判断redis里面是否有数据，存在则判定无需向MySQl查找数据，直接返回
        if (dishes != null && dishes.size() > 0) {
            log.info("在redis查找对应的菜品数据： {}", dishes);
            return Result.success(dishes);
        }

        //不存在则向MySQL查找数据，并放入redis缓存中 数据格式 key——string
        dishes = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(key, dishes);

        return Result.success(dishes);
    }


}
