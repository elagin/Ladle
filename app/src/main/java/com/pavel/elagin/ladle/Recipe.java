package com.pavel.elagin.ladle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by elagin on 30.11.16.
 */

public class Recipe implements Serializable {

    public class Ingredient implements Serializable {
        public String name;
        public int count;

        public Ingredient() {
        }

        public Ingredient(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    private String name;
    private String description;
    private int totalTime;

    private List<Ingredient> ingredients;

    public Recipe() {
        ingredients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void addIngredient(String name, int count) {
        ingredients.add(new Ingredient(name, count));
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void clearIngredients() {
        ingredients.clear();
    }
}
