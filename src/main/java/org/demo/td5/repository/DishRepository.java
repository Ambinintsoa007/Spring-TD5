package org.demo.td5.repository;

import org.demo.td5.config.DataSource;
import org.demo.td5.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishRepository {

    private final DataSource dataSource = new DataSource();

    public List<Dish> findAll() {
        Connection connection = dataSource.getConnection();
        List<Dish> dishes = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id, name, selling_price FROM dish;"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setPrice(rs.getDouble("selling_price"));
                dish.setDishIngredients(findIngredientsByDishId(rs.getInt("id")));
                dishes.add(dish);
            }
            return dishes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    private List<DishIngredient> findIngredientsByDishId(Integer dishId) {
        Connection connection = dataSource.getConnection();
        List<DishIngredient> dishIngredients = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("""
                SELECT ingredient.id, ingredient.name, ingredient.price, ingredient.category,
                       di.required_quantity, di.unit
                FROM ingredient
                JOIN dish_ingredient di ON di.id_ingredient = ingredient.id
                WHERE di.id_dish = ?;
                """);
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                DishIngredient dishIngredient = new DishIngredient();
                dishIngredient.setIngredient(ingredient);
                dishIngredient.setQuantity(rs.getDouble("required_quantity"));
                dishIngredient.setUnit(Unit.valueOf(rs.getString("unit")));
                dishIngredients.add(dishIngredient);
            }
            return dishIngredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }
}