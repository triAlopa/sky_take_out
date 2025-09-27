package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询分类
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询分类")
    public Result<PageResult<Category>> queryPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("查询分类参数: {}", categoryPageQueryDTO);

        PageResult<Category> list = categoryService.queryPage(categoryPageQueryDTO);

        return Result.success(list);
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List> list(Integer type) {
        log.info("根据类型查询分类 : {}", type);

        List<Category> list = categoryService.list(type);

        return Result.success(list);
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增分类")
    public Result<List> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类 : {}", categoryDTO);

        categoryService.addCategory(categoryDTO);

        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类 : {}", categoryDTO);

        categoryService.updateCategory(categoryDTO);

        return Result.success();
    }

    /**
     * 修改分类状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改分类状态")
    public Result setStatus(@PathVariable Integer status, Long id) {
        log.info("修改分类状态 : {} ,{}", id, status);

        categoryService.updateStatus(status,id);

        return Result.success();
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping()
    @ApiOperation("根据id删除分类")
    public Result deleteById(Long id){
        log.info("删除分类id ： {}",id);

        categoryService.delById(id);

        return Result.success();
    }
}
