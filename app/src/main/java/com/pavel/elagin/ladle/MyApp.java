package com.pavel.elagin.ladle;

import android.app.Application;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavel on 25.11.16.
 */

public class MyApp extends Application {
    private static MyApp instance;

    private static List<Recipe> recipes;

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

    public static List<Recipe> getRecipes() {
        return recipes;
    }

}
