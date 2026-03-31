package org.demo.td5.controller;

import org.demo.td5.entity.Ingredient;
import org.demo.td5.entity.StockValue;
import org.demo.td5.entity.Unit;
import org.demo.td5.service.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<?> getAllIngredients() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ingredientService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable Integer id) {
        try {
            Ingredient ingredient = ingredientService.findById(id);
            if (ingredient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingredient.id=" + id + " is not found");
            }
            return ResponseEntity.status(HttpStatus.OK).body(ingredient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getIngredientStock(
            @PathVariable Integer id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {

        if (at == null || unit == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        try {
            Ingredient ingredient = ingredientService.findById(id);
            if (ingredient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingredient.id=" + id + " is not found");
            }
            StockValue stockValue = ingredientService.getStockValueAt(id, Instant.parse(at), Unit.valueOf(unit));
            return ResponseEntity.status(HttpStatus.OK).body(stockValue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}