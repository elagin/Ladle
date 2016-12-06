package com.pavel.elagin.ladle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pavel.elagin.ladle.Activites.EditRecActivity;
import com.pavel.elagin.ladle.Activites.ViewRecActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pavel on 25.11.16.
 */

public class MyApp extends Application {
    private static MyApp instance;
    private static Activity currentActivity;
    private static List<Recipe> recipes;
    private final static Random random = new Random();

    final static String fileNameRecipes = "recipe_list.txt";
    final static String fileNameRecipesJSon = "recipe_list_json.txt";
    final static String exportFolderName = "ladle";

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

    public static void saveRecipesJSon(boolean isLocal) {
        FileOutputStream fos = null;
        try {
            if (isLocal) {
                fos = getAppContext().openFileOutput(fileNameRecipesJSon, Context.MODE_PRIVATE);
            } else {
                if (isExternalStorageWritable()) {
                    fos = new FileOutputStream(getExternalFileName());
                } else {
                    //todo: карта памяти отсутствует.
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(fos);
            os.writeObject(getJSonData());
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveRecipes();
    }

    private static String getJSonData() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(new RecipeJsonDataHolder(recipes));
        return json;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
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
            } catch (java.io.InvalidClassException e) {
                File file = new File(getAppContext().getFilesDir(), fileNameRecipes);
                file.delete();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadRecipesJSon(boolean isLocal) {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        try {
            if (isLocal) {
                fis = getAppContext().openFileInput(fileNameRecipesJSon);
            } else {
                if (isExternalStorageReadable())
                    fis = new FileInputStream(getExternalFileName());
            }
            is = new ObjectInputStream(fis);
            String json = (String) is.readObject();
            RecipeJsonDataHolder holder = new Gson().fromJson(json, RecipeJsonDataHolder.class);
            recipes = holder.mContactList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (is != null) {
            try {
                is.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class RecipeJsonDataHolder {
        public List<Recipe> mContactList;

        public RecipeJsonDataHolder(List<Recipe> mContactList) {
            this.mContactList = mContactList;
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

    public static File getExternalFileName() {
        //todo: Как сделать работу с /storage/sdcard1 ?
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), exportFolderName);
        File file = new File(dir.getAbsolutePath() + "/" + fileNameRecipesJSon);
        return file;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        MyApp.currentActivity = currentActivity;
    }

    public static void toDetails(int id) {
        Intent intent = new Intent(getAppContext(), ViewRecActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("recipeID", id);
        intent.putExtras(bundle);
        getCurrentActivity().startActivity(intent);
    }

    public static void toEdit(int id) {
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
