package com.shop.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemUpdateDto {
    private Integer quantity;
    private BigDecimal price;
}