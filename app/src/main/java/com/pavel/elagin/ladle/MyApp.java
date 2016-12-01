package com.pavel.elagin.ladle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.pavel.elagin.ladle.Activites.EditRecActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pavel on 25.11.16.
 */

public class MyApp extends Application {
    private static MyApp instance;
    private static Activity currentActivity;
    private static List<Recipe> recipes;
    private final static Random random = new Random();

    final static String fileNameRecipes = "recipe_list.txt";

    static {
        //currentActivity = null;
    }

    {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        recipes = new ArrayList<>();
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static void saveRecipes() {
        FileOutputStream fos = null;
        try {
            fos = getAppContext().openFileOutput(fileNameRecipes, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(fos);
            os.writeObject(recipes);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadRecipes() {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        try {
            fis = getAppContext().openFileInput(fileNameRecipes);
            is = new ObjectInputStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null) {
            try {
                recipes = (List<Recipe>) is.readObject();
                is.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static final List<Recipe> getRecipes() {
        return recipes;
    }

    public static Recipe getRecipe(int uid) {
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            if (recipe.getUid().equals(uid))
                return recipe;
        }
        return null;
    }

    public static void updateRecipe(Recipe recipe) {
        for (int i = 0; i < recipes.size(); i++) {
            Recipe oldRecipe = recipes.get(i);
            if (oldRecipe.getUid().equals(recipe.getUid())) {
                recipes.set(i, recipe);
                return;
            }
        }
        recipes.add(recipe);
    }

    public static void removeRecipe(int uid) {
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            if (recipe.getUid().equals(uid)) {
                recipes.remove(i);
                return;
            }
        }
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        MyApp.currentActivity = currentActivity;
    }

    public static void toDetails(int id) {
        Intent intent = new Intent(getAppContext(), EditRecActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("recipeID", id);
        intent.putExtras(bundle);
        getCurrentActivity().startActivity(intent);
    }

    public static int newId() {
        return random.nextInt(Integer.MAX_VALUE);
    }
}
