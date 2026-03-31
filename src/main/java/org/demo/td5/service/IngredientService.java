package org.demo.td5.service;

import org.demo.td5.entity.Ingredient;
import org.demo.td5.entity.StockValue;
import org.demo.td5.entity.Unit;
import org.demo.td5.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> findAll() {
        return ingredientRepository.findAllIngredient();
    }

    public Ingredient findById(Integer id) {
        return ingredientRepository.findById(id);
    }

    public StockValue getStockValueAt(Integer id, Instant at, Unit unit) {
        return ingredientRepository.getStockValueAt(id, at, unit);
    }
}