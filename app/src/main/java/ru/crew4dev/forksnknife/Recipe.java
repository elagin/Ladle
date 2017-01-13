package ru.crew4dev.forksnknife;

import java.io.File;
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

    public class Step {
        public String fileName;
        public String desc;
        public Integer time;

        public Step(String fileName, String desc, Integer time) {
            this.fileName = fileName;
            this.desc = desc;
            this.time = time;
        }
    }

    private Integer uid;
    private String name;
    private String description;
    private String steps;
    private Integer totalTime;
    private String photo;
    private String tags;

    private List<Ingredient> ingredients;

    private List<Step> stepList;

    public Recipe() {
        ingredients = new ArrayList<>();
        stepList = new ArrayList<>();
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

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public void addIngredient(String name, double count, String unit) {
        ingredients.add(new Ingredient(name, count, unit));
    }

    public void addStep(String fileName, String desc, Integer time) {
        stepList.add(new Step(fileName, desc, time));
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    public void clearIngredients() {
        ingredients.clear();
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void deleteStep(int id) {
        if (stepList.size() > id) {
            Step step = stepList.get(id);
            if (step.fileName != null && step.fileName.length() > 0) {
                File file = new File(step.fileName);
                boolean deleted = file.delete();
            }
            stepList.remove(id);
        }
    }

    private int getTotalStepTime() {
        int res = 0;
        for (int i = 0; i < stepList.size(); i++) {
            Step step = stepList.get(i);
            if (step.time != null)
                res = res + step.time;
        }
        return res;
    }

    public String getTotalStepTimeString() {
        StringBuilder res = new StringBuilder();
        int totalStepTime = getTotalStepTime();
        int totalTime = 0;

        if (getTotalTime() != null && getTotalTime().toString().length() > 0)
            totalTime = getTotalTime();

        if (totalTime > 0) {
            res.append(totalTime);
            if (totalStepTime > 0) {
                res.append(" (");
                res.append(totalStepTime);
                res.append(")");
            }
        } else if (totalStepTime > 0) {
            res.append(" (");
            res.append(totalStepTime);
            res.append(")");
        }
        return res.toString();
    }
}
