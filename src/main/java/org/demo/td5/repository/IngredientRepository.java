package org.demo.td5.repository;

import org.demo.td5.config.DataSource;
import org.demo.td5.entity.CategoryEnum;
import org.demo.td5.entity.Ingredient;
import org.demo.td5.entity.StockValue;
import org.demo.td5.entity.Unit;

import java.sql.*;
import java.time.Instant;
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

    public Ingredient findById(Integer id) {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT id, name, category, price FROM ingredient WHERE id = ?;";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredient.setPrice(rs.getDouble("price"));
                return ingredient;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public StockValue getStockValueAt(Integer id, Instant at, Unit unit) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("""
            SELECT
                unit,
                SUM(CASE
                    WHEN type = 'IN' THEN quantity
                    WHEN type = 'OUT' THEN -quantity
                    ELSE 0
                END) as actual_quantity
            FROM stock_movement
            WHERE id_ingredient = ?
              AND creation_datetime <= ?
              AND unit = ?::unit_type
            GROUP BY unit;
            """);
            ps.setInt(1, id);
            ps.setTimestamp(2, Timestamp.from(at));
            ps.setString(3, unit.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StockValue stockValue = new StockValue();
                stockValue.setUnit(Unit.valueOf(rs.getString("unit")));
                stockValue.setQuantity(rs.getDouble("actual_quantity"));
                return stockValue;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }
}