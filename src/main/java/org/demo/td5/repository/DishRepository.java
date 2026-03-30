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

    public Dish findById(Integer id) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id, name, selling_price FROM dish WHERE id = ?;"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setPrice(rs.getDouble("selling_price"));
                dish.setDishIngredients(findIngredientsByDishId(rs.getInt("id")));
                return dish;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public Dish updateIngredients(Integer dishId, List<Ingredient> ingredients) {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);

            // Construire la liste DishIngredient avec uniquement les ingrédients existants en DB
            List<DishIngredient> validDishIngredients = new ArrayList<>();
            for (Ingredient ingredient : ingredients) {
                PreparedStatement checkPs = connection.prepareStatement(
                        "SELECT id, name, price, category FROM ingredient WHERE id = ?;"
                );
                checkPs.setInt(1, ingredient.getId());
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    // On prend les données réelles de la DB, on ignore ce qui est fourni
                    Ingredient dbIngredient = new Ingredient();
                    dbIngredient.setId(rs.getInt("id"));
                    dbIngredient.setName(rs.getString("name"));
                    dbIngredient.setPrice(rs.getDouble("price"));
                    dbIngredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                    Dish dish = new Dish();
                    dish.setId(dishId);

                    DishIngredient dishIngredient = new DishIngredient();
                    dishIngredient.setIngredient(dbIngredient);
                    dishIngredient.setDish(dish);
                    dishIngredient.setQuantity(1.0); // valeur par défaut
                    dishIngredient.setUnit(Unit.PCS); // valeur par défaut
                    validDishIngredients.add(dishIngredient);
                }
            }

            // Détacher tous les ingrédients actuels du plat
            PreparedStatement deletePs = connection.prepareStatement(
                    "DELETE FROM dish_ingredient WHERE id_dish = ?;"
            );
            deletePs.setInt(1, dishId);
            deletePs.executeUpdate();

            // Attacher les ingrédients valides
            attachIngredients(connection, validDishIngredients);

            connection.commit();
            return findById(dishId);
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { throw new RuntimeException(ex); }
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
                dishIngredient.setQuantity(rs.getObject("required_quantity") == null ? null : rs.getDouble("required_quantity"));
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

    private void attachIngredients(Connection conn, List<DishIngredient> ingredients) throws SQLException {
        if (ingredients == null || ingredients.isEmpty()) return;

        String attachSql = """
        INSERT INTO dish_ingredient (id, id_ingredient, id_dish, required_quantity, unit)
        VALUES (nextval('dish_ingredient_id_seq'), ?, ?, ?, ?::unit_type)
        """;
        try (PreparedStatement ps = conn.prepareStatement(attachSql)) {
            for (DishIngredient dishIngredient : ingredients) {
                ps.setInt(1, dishIngredient.getIngredient().getId());
                ps.setInt(2, dishIngredient.getDish().getId());
                ps.setDouble(3, dishIngredient.getQuantity());
                ps.setObject(4, dishIngredient.getUnit().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT pg_get_serial_sequence(?, ?)")) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) throw new IllegalArgumentException("No sequence found for " + tableName + "." + columnName);

        String setValSql = String.format("SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))", sequenceName, columnName, tableName);
        try (PreparedStatement ps = conn.prepareStatement(setValSql)) { ps.executeQuery(); }

        try (PreparedStatement ps = conn.prepareStatement("SELECT nextval(?)")) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}