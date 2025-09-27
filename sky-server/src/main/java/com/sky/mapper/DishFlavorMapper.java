package com.sky.mapper;

import com.sky.annotation.autoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量添加口味
     * @param flavors
     */
    void insertList(List<DishFlavor> flavors);
}
