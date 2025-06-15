package com.shop.controller;

import com.shop.dto.ItemDto;
import com.shop.dto.ItemUpdateDto;
import com.shop.entity.Category;
import com.shop.entity.Item;
import com.shop.repository.CategoryRepository;
import com.shop.repository.ItemRepository;
import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setQuantity(dto.getQuantity());
        item.setCategory(category);
        
        return ResponseEntity.ok(itemRepository.save(item));
    }
    
    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    @GetMapping("/category/{categoryId}")
    public List<Item> getItemsByCategory(@PathVariable Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody ItemUpdateDto dto) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));

        if (dto.getQuantity() != null) {
            item.setQuantity(item.getQuantity() + dto.getQuantity()); // Изменение количества товара (+/-)
        }
        
        if (dto.getPrice() != null) {
            item.setPrice(dto.getPrice());
        }

        if (dto.getCategoryId() != null) {
            System.out.println("Trying to update category to: " + dto.getCategoryId());
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));
            item.setCategory(category);
            System.out.println("Category updated to: " + category.getName());
        }

        Item savedItem = itemRepository.save(item);
        System.out.println("Item saved: " + savedItem);
        return ResponseEntity.ok(itemRepository.save(item));
    }

}