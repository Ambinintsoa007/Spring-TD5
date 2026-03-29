package org.demo.td5.entity;

import java.time.LocalDate;
import java.util.Map;

public class StockStatistic {
    private Integer ingredientId;
    private String ingredientName;
    private Map<LocalDate, Double> statistics; // Date -> Quantité

    public Integer getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Map<LocalDate, Double> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<LocalDate, Double> statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
        return "StockStatistic{" +
                "ingredientId=" + ingredientId +
                ", ingredientName='" + ingredientName + '\'' +
                ", statistics=" + statistics +
                '}';
    }
}