package org.demo.td5.controller;

import org.demo.td5.entity.Dish;
import org.demo.td5.entity.Ingredient;
import org.demo.td5.repository.DishRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishRepository dishRepository = new DishRepository();

    @GetMapping
    public ResponseEntity<?> getAllDishes() {
        try {
            List<Dish> dishes = dishRepository.findAll();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(dishes);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable Integer id,
            @RequestBody(required = false) List<Ingredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Request body is required.");
        }

        try {
            Dish dish = dishRepository.findById(id);
            if (dish == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Dish.id=" + id + " is not found");
            }

            Dish updated = dishRepository.updateIngredients(id, ingredients);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}