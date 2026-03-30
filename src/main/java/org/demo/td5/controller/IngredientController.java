package org.demo.td5.controller;

import org.demo.td5.entity.Ingredient;
import org.demo.td5.entity.StockValue;
import org.demo.td5.entity.Unit;
import org.demo.td5.repository.IngredientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository = new IngredientRepository();

    @GetMapping
    public ResponseEntity<?> getAllIngredients() {
        try {
            List<Ingredient> ingredients = ingredientRepository.findAllIngredient();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ingredients);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable Integer id) {
        try {
            Ingredient ingredient = ingredientRepository.findById(id);
            if (ingredient == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Ingredient.id=" + id + " is not found");
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ingredient);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getIngredientStock(
            @PathVariable Integer id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {

        if (at == null || unit == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        try {
            Ingredient ingredient = ingredientRepository.findById(id);
            if (ingredient == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Ingredient.id=" + id + " is not found");
            }

            Instant instant = Instant.parse(at);
            Unit unitEnum = Unit.valueOf(unit);
            StockValue stockValue = ingredientRepository.getStockValueAt(id, instant, unitEnum);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(stockValue);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}