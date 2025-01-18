package com.example.healthandfitness.Activities;

import com.example.healthandfitness.Models.FoodItem;

import java.util.List;

public class NutritionResponse {
    private List<FoodItem> items;

    public List<FoodItem> getItems() {
        return items;
    }

    public void setItems(List<FoodItem> items) {
        this.items = items;
    }
}
