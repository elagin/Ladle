package ru.crew4dev.forksnknife;

import java.io.Serializable;

/**
 * Created by pavel on 12.02.2017.
 */

public class Ingredient implements Serializable {
    public String name;
    public Double count;
    public String unit;

    public Ingredient() {
    }

        public Ingredient(Ingredient value) {
            this.name = value.name;
            this.count = value.count;
            this.unit = value.unit;
        }

    public Ingredient(String name, double count, String unit) {
        this.name = name;
        this.count = count;
        this.unit = unit;
    }
}

