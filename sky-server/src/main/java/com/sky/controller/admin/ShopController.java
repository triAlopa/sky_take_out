package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺状态接口")
public class ShopController {
    public final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("设置状态")
    public Result setShopStatus(@PathVariable Integer status) {

        log.info("设置店铺状态为 ：{}", status == 1 ? "营业中" : "已打烊");

        ValueOperations valueOperations = redisTemplate.opsForValue();

        valueOperations.set(KEY, status);

        return Result.success();
    }



    @GetMapping("/status")
    @ApiOperation("获取状态")
    public Result getShopStatus() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get(KEY);

        log.info("获取店铺状态为 ：{}", status == 1 ? "营业中" : "已打烊");

        return Result.success(status);
    }
}
