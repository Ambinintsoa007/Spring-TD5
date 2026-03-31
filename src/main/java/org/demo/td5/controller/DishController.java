package org.demo.td5.controller;

import org.demo.td5.entity.Dish;
import org.demo.td5.entity.Ingredient;
import org.demo.td5.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<?> getAllDishes() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(dishService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable Integer id,
            @RequestBody(required = false) List<Ingredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is required.");
        }

        try {
            Dish dish = dishService.findById(id);
            if (dish == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dish.id=" + id + " is not found");
            }
            return ResponseEntity.status(HttpStatus.OK).body(dishService.updateIngredients(id, ingredients));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/ingredients")
    public ResponseEntity<?> getDishIngredients(
            @PathVariable Integer id,
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) Double ingredientPriceAround) {
        try {
            Dish dish = dishService.findById(id);
            if (dish == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dish.id=" + id + " is not found");
            }
            return ResponseEntity.status(HttpStatus.OK).body(dishService.findIngredientsByDishIdWithFilters(id, ingredientName, ingredientPriceAround));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}