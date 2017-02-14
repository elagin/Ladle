package ru.crew4dev.forksnknife;

/**
 * Created by elagin on 14.02.17.
 */

public class Purchase {
    private boolean isBought;
    private String name;
    private Double count;
    private String unit;

    public Purchase(Ingredient value) {
        this.name = value.name;
        this.count = value.count;
        this.unit = value.unit;
    }

    public Boolean isBought(){
        return isBought;
    }

    public void toggle(){
        isBought = !isBought;
    }

    public String getName() {
        return name;
    }

    public String getCount() {
        return count.toString().replace(".0", "");
    }

    public String getUnit() {
        return unit;
    }

}
