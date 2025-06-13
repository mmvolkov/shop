package com.shop.controller;

import com.shop.dto.CategoryDto;
import com.shop.entity.Category;
import com.shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;
    
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        
        return ResponseEntity.ok(categoryRepository.save(category));
    }
    
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        return categoryRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}