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

    public class Step {
        public String fileName;
        public String desc;
        public Integer time;

        public Step(String fileName, String desc, Integer time) {
            this.fileName = fileName;
            this.desc = desc;
            this.time = time;
        }

        public Step(Step step) {
            this.desc = step.desc;
            this.time = step.time;
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

    public Recipe(Recipe recipe) {
        this.name = recipe.name;
        this.description = recipe.description;
        this.totalTime = recipe.totalTime;
        this.tags = recipe.tags;
        this.ingredients = recipe.ingredients;
        this.stepList = new ArrayList<>(recipe.stepList);
//        try {
//            this.stepList = recipe.stepList.
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        Collections.copy(this.stepList, recipe.stepList);
//
        //this.stepList = new CopyOnWriteArrayList<>(recipe.stepList);
//        for (Step step : this.stepList) {
//            step.fileName = null;
//        }
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

    public Step getStep(Integer id) {
        return stepList.get(id);
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

    public void deleteStep(Integer id) {
        if (stepList.size() > id) {
            Step step = stepList.get(id);
            if (step.fileName != null && step.fileName.length() > 0) {
                File file = new File(step.fileName);
                file.delete();
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
