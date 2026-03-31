package org.demo.td5.service;

import org.demo.td5.entity.Dish;
import org.demo.td5.entity.DishIngredient;
import org.demo.td5.entity.Ingredient;
import org.demo.td5.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Dish findById(Integer id) {
        return dishRepository.findById(id);
    }

    public Dish updateIngredients(Integer id, List<Ingredient> ingredients) {
        return dishRepository.updateIngredients(id, ingredients);
    }

    public List<DishIngredient> findIngredientsByDishIdWithFilters(Integer id, String ingredientName, Double ingredientPriceAround) {
        return dishRepository.findIngredientsByDishIdWithFilters(id, ingredientName, ingredientPriceAround);
    }
}