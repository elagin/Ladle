package com.pavel.elagin.ladle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by elagin on 30.11.16.
 */

public class Recipe implements Serializable {

    private static final String TAG = "myLogs";

    public class Ingredient implements Serializable {
        public String name;
        public double count;
        public String unit;

        public Ingredient() {
        }

        public Ingredient(String name, double count, String unit) {
            this.name = name;
            this.count = count;
            this.unit = unit;
        }
    }

    private Integer uid;
    private String name;
    private String description;
    private String steps;
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

    public void addIngredient(String name, double count, String unit) {
        ingredients.add(new Ingredient(name, count, unit));
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void clearIngredients() {
        ingredients.clear();
    }
}
