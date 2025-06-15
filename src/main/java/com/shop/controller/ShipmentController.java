package com.shop.controller;

import com.shop.dto.ShipmentDto;
import com.shop.entity.Item;
import com.shop.entity.User;
import com.shop.repository.ItemRepository;
import com.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipment")
public class ShipmentController {
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> shipItem(@RequestHeader("X-API-KEY") String apiKey, 
                                    @RequestBody ShipmentDto dto) {

        User user = userRepository.findByApiKey(apiKey)
            .orElseThrow(() -> new RuntimeException("Invalid API key"));
        
        Item item = itemRepository.findById(dto.getItemId())
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (item.getQuantity() < dto.getQuantity()) {
            return ResponseEntity.badRequest().body("Insufficient quantity");
        }
        
        item.setQuantity(item.getQuantity() - dto.getQuantity());
        itemRepository.save(item);
        
        return ResponseEntity.ok("Shipment processed successfully");
    }
}