package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "用户购物车相关接口")
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户购物车添加数据接口
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("用户购物车添加数据接口")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("用户购物车添加数据: {}", shoppingCartDTO);


        shoppingCartService.save(shoppingCartDTO);

        return Result.success();
    }

    /**
     * 用户购物车查询数据接口
     * @return
     */
    @ApiOperation("用户购物车查询数据接口")
    @GetMapping("/list")
    public Result list() {
        Long userId = BaseContext.getCurrentId();

        log.info("用户{} 购物车添加数据: ", userId);

        List<ShoppingCart> list= shoppingCartService.selectByUserId(userId);

        return Result.success(list);
    }

    /**
     *  用户购物车清空数据接口
     * @return
     */
    @ApiOperation("用户购物车清空数据接口")
    @DeleteMapping("/clean")
    public Result clean() {
        Long userId = BaseContext.getCurrentId();

        log.info("用户{} 购物车清空数据: ", userId);

        shoppingCartService.clean(userId);

        return Result.success();
    }

    /**
     * 用户购物车删除数据接口
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("用户购物车删除数据接口")
    @PostMapping("/sub")
    public Result delItem(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("用户购物车删除一项数据: {}", shoppingCartDTO);

        shoppingCartService.delItem(shoppingCartDTO);

        return Result.success();
    }
}
