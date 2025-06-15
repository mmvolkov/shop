package com.shop.dto;

import com.shop.entity.Category;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class ItemUpdateDto {
    private Integer quantity;
    private BigDecimal price;

    @Setter
    @Getter
    private Long categoryId;

    @Override
    public String toString() {
        return "ItemUpdateDto{quantity=" + quantity + ", price=" + price + ", categoryId=" + categoryId + "}";
    }

}