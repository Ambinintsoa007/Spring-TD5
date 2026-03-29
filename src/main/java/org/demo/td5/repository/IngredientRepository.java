package org.demo.td5.repository;

import org.demo.td5.config.DataSource;
import org.demo.td5.entity.CategoryEnum;
import org.demo.td5.entity.Ingredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientRepository {

    private final DataSource dataSource = new DataSource();

    public List<Ingredient> findAllIngredient() {
        Connection connection = dataSource.getConnection();
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT id, name, category, price FROM ingredient;";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredient.setPrice(rs.getDouble("price"));
                ingredients.add(ingredient);
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }
}