package com.shop.dto;

import com.shop.entity.Category;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
}