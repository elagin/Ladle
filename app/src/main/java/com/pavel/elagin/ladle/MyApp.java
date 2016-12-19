package com.pavel.elagin.ladle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pavel.elagin.ladle.Activites.AboutActivity;
import com.pavel.elagin.ladle.Activites.EditRecActivity;
import com.pavel.elagin.ladle.Activites.ViewRecActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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

    private final static String fileNameRecipes = "recipe_list.txt";
    private final static String fileNameRecipesJSon = "recipe_list_json.txt";
    private final static String exportFolderName = "Ladle";

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

    private static void saveRecipes() {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;

        try {
            fos = getAppContext().openFileOutput(fileNameRecipes, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(recipes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean saveRecipesJSon(Context context, boolean isLocal) {
        boolean res = false;
        FileOutputStream fos = null;
        ObjectOutputStream os = null;

        try {
            if (isLocal) {
                fos = getAppContext().openFileOutput(fileNameRecipesJSon, Context.MODE_PRIVATE);
            } else {
                if (isExternalStorageWritable()) {
                    fos = new FileOutputStream(getExternalFileName(true));
                } else {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    Toast.makeText(context, String.format(context.getString(R.string.access_error), path), Toast.LENGTH_LONG).show();
                    return res;
                }
            }
            if (fos != null) {
                os = new ObjectOutputStream(fos);
                os.writeObject(getJSonData());
                res = true;
                saveRecipes();
                return res;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return res;
        } finally {
            try {
                if (os != null)
                    os.close();
                if (fos != null)
                    fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private static String getJSonData() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(new RecipeJsonDataHolder(recipes));
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
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
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean loadRecipesJSon(boolean isLocal) {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        try {
            if (isLocal) {
                fis = getAppContext().openFileInput(fileNameRecipesJSon);
            } else {
                if (isExternalStorageReadable())
                    fis = new FileInputStream(getExternalFileName(false));
            }
            is = new ObjectInputStream(fis);
            String json = (String) is.readObject();
            RecipeJsonDataHolder holder = new Gson().fromJson(json, RecipeJsonDataHolder.class);
            recipes = holder.mContactList;
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static class RecipeJsonDataHolder {
        public List<Recipe> mContactList;

        public RecipeJsonDataHolder(List<Recipe> mContactList) {
            this.mContactList = mContactList;
        }
    }

    public static List<Recipe> getRecipes() {
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
                if (recipe.getPhoto() != null && recipe.getPhoto().length() > 0) {
                    File file = new File(recipe.getPhoto());
                    boolean deleted = file.delete();
                }
                recipes.remove(i);
                return;
            }
        }
    }

    private static File getExternalFileName(boolean isCreate) {
        //todo: Как сделать работу с /storage/sdcard1 ?
        File dir = getDataFolder();
        if (!dir.exists() && isCreate) {
            if (!dir.mkdir()) {
                return null;
            }
        }
        File file = new File(dir.getAbsolutePath() + "/" + fileNameRecipesJSon);
        return file;
    }

    public static File getDataFolder() {
        return new File(Environment.getExternalStorageDirectory() + "/" + exportFolderName);
    }

    private static Activity getCurrentActivity() {
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

    public static void toAbout() {
        Intent intent = new Intent(getAppContext(), AboutActivity.class);
        getCurrentActivity().startActivity(intent);
    }

    public static int newId() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        try {
            bm = BitmapFactory.decodeFile(path, options);
            return bm;
        } catch (OutOfMemoryError e) {
            try {
                //options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                return bitmap;
            } catch (Exception ex) {
                //Log.d(TAG, String.valueOf(ex));
            }
        }
        return null;
    }

    public static boolean setPic(String mCurrentPhotoPath, ImageView view) {
        //ImageView mImageView = (ImageView) findViewById(R.id.imageView2);
        // Get the dimensions of the View
        //View parent = (View) view.getParent();

        //ViewParent parent = view.getParent();
        //ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();

        int targetW = view.getWidth();
        if (targetW == 0)
            targetW = 100;
        int targetH = view.getHeight();
        if (targetH == 0)
            targetH = 100;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            //view.setLayoutParams(new LinearLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
            return true;
        }
        return false;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static Uri getNewFileName() throws IOException {
        Long timestamp = System.currentTimeMillis();
        String newFileName = timestamp.toString();
        String file = MyApp.getDataFolder().getAbsolutePath() + "/" + newFileName + ".jpg";
        File newfile = new File(file);
        newfile.createNewFile();
        Uri outputFileUri = Uri.fromFile(newfile);
        return outputFileUri;
    }
}
