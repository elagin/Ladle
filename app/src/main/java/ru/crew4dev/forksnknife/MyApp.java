package ru.crew4dev.forksnknife;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import ru.crew4dev.forksnknife.Activites.AboutActivity;
import ru.crew4dev.forksnknife.Activites.CoockingActivity;
import ru.crew4dev.forksnknife.Activites.EditRecActivity;
import ru.crew4dev.forksnknife.Activites.SettingsActivity;
import ru.crew4dev.forksnknife.Activites.ViewRecActivity;

/**
 * Created by pavel on 25.11.16.
 */

public class MyApp extends Application {
    private static MyApp instance;
    private static Activity currentActivity;
    private static List<Recipe> recipes;
    private final static Random random = new Random();

    public static boolean permissionRequested = false;
    public static final int SDCARD_PERMISSION = 1;

    public static final int DATA_STORAGE = 0;
    public static final int INTERNAL_STORAGE = 1;
    public static final int EXTERNAL_STORAGE = 2;

    private static Map<Integer, String> storages = new HashMap<>();

    //    private final static String fileNameRecipes = "recipe_list.txt";
    private final static String fileNameRecipesJSon = "recipe_list_json.txt";
    private final static String exportFolderName = "Forksnknife";

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
        new Preferences(this);
        readStorages();
    }

    private static void readStorages() {
        File dataDir = MyApp.getAppContext().getFilesDir();
        if (dataDir != null && dataDir.canWrite())
            storages.put(DATA_STORAGE, dataDir.getAbsolutePath());
        File internal = MyApp.getInternalStorage();
        if (internal != null && internal.canWrite()) {
            storages.put(INTERNAL_STORAGE, internal.getAbsolutePath());
        }
        File external = MyApp.getExternalStorage();
        if (external != null && external.canWrite()) {
            storages.put(EXTERNAL_STORAGE, external.getAbsolutePath());
        }
    }

    public static Map<Integer, String> getStorages() {
        return storages;
    }


    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static String getFileDir() {
        return instance.getApplicationContext().getFilesDir().getAbsolutePath();
    }

    /*
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
    */
    public static boolean saveRecipesJSon(Context context, boolean isLocal) {
        BufferedOutputStream fos = null;
        ObjectOutputStream os = null;

        try {
            if (isLocal) {
                //fos = new BufferedOutputStream(getAppContext().openFileOutput(fileNameRecipesJSon, Context.MODE_PRIVATE));
                fos = new BufferedOutputStream(new FileOutputStream(Preferences.getSyncFolder() + File.separator + fileNameRecipesJSon));
            } else {
//                if (isExternalStorageWritable()) {
////                    exportPhotos();
//                    fos = new BufferedOutputStream(new FileOutputStream(getExternalFileName(true)));
//                } else {
//                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//                    Toast.makeText(context, String.format(context.getString(R.string.error_access), path), Toast.LENGTH_LONG).show();
//                    return false;
//                }
            }
            os = new ObjectOutputStream(fos);
            os.writeObject(getJSonData());
            //saveRecipes();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return false;
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

    private static String getJSonData() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /*
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
    */

    public static boolean loadRecipesJSon(boolean isLocal, Context context) {
        BufferedInputStream fis = null;
        ObjectInputStream is = null;
        if (!permissionGranted())
            return false;
        try {
            if (isLocal) {
                //fis = new BufferedInputStream(getAppContext().openFileInput(fileNameRecipesJSon));
                fis = new BufferedInputStream(new FileInputStream(Preferences.getSyncFolder() + File.separator + fileNameRecipesJSon));
            } else {
//                if (isExternalStorageReadable())
//                    fis = new BufferedInputStream(new FileInputStream(getExternalFileName(false)));
            }
            is = new ObjectInputStream(fis);
            String json = (String) is.readObject();
            RecipeJsonDataHolder holder = new Gson().fromJson(json, RecipeJsonDataHolder.class);
            if (holder.mContactList.size() > 0)
                recipes = holder.mContactList;
            return true;

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
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

    public static boolean removeRecipe(int uid) {
        boolean res = true;
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            if (recipe.getUid().equals(uid)) {
                if (recipe.getPhoto() != null && recipe.getPhoto().length() > 0) {
                    res = MyApp.fileDelete(recipe.getPhoto());
                }
                recipes.remove(i);
                return res;
            }
        }
        return true;
    }

    /*
        private static final long K = 1024;
        private static final long M = K * K;
        private static final long G = M * K;
        private static final long T = G * K;

        public static String convertToStringRepresentation(final long value) {
            final long[] dividers = new long[]{T, G, M, K, 1};
            final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};
            String result = null;
            if (value < 1) {
                return value + " " + units[units.length - 1];
            }
            for (int i = 0; i < dividers.length; i++) {
                final long divider = dividers[i];
                if (value >= divider) {
                    result = format(value, divider, units[i]);
                    break;
                }
            }
            return result;
        }

        private static String format(final long value, final long divider, final String unit) {
            final double result = divider > 1 ? (double) value / (double) divider : (double) value;
            return String.format("%.1f %s", Double.valueOf(result), unit);
        }
    */
//    private static File getExternalFileName(boolean isCreate) {
//        //todo: Как сделать работу с /storage/sdcard1 ?
//        File dir = new File(Preferences.getSyncFolder());
//        if (!dir.exists() && isCreate) {
//            if (!dir.mkdir()) {
//                return null;
//            }
//        }
//        /*
//        try {
//            long folderSize = folderSize(dir.getCanonicalFile());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//*/
//        return new File(dir.getAbsolutePath() + File.separator + fileNameRecipesJSon);
//    }

//    public static List<String> getStoreList() {
//        List<String> res = new ArrayList<>();
//        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
//        List<String> pathList = getExternalMounts();
//        for (String item : pathList) {
//            String sdCard = pathList.get(0).substring(pathList.get(0).lastIndexOf(File.separator));
//            String path = externalStorageDirectory.substring(0, externalStorageDirectory.lastIndexOf(File.separator));
//            res.add(path + sdCard + File.separator + exportFolderName);
//        }
//        return res;
//    }

    public static File getInternalStorage() {
        return new File(Environment.getExternalStorageDirectory() + File.separator + exportFolderName);
    }


    public static File getExternalStorage() {
//        String extStore = System.getenv("EXTERNAL_STORAGE");
//        String secStore = System.getenv("SECONDARY_STORAGE");
//        String strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
//        String strSDCardPath2 = System.getenv("MEDIA_STORAGE");
//        String strSDCardPath3 = System.getenv("ENV_MEDIA_STORAGE");
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        //externalStorageDirectory.substring(externalStorageDirectory.lastIndexOf(File.separator))+ File.separator + pathList.get(0);
//        return new File(Environment.getExternalStorageDirectory() + File.separator + exportFolderName);

        List<String> pathList = getExternalMounts();
        if (pathList.size() > 0) {
            String sdCard = pathList.get(0).substring(pathList.get(0).lastIndexOf(File.separator));
            String path = externalStorageDirectory.substring(0, externalStorageDirectory.lastIndexOf(File.separator));
            return new File(path + sdCard + File.separator + exportFolderName);
        } else
            return null;
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
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

    public static void toCoocking(int id) {
        Intent intent = new Intent(getAppContext(), CoockingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("recipeID", id);
        intent.putExtras(bundle);
        getCurrentActivity().startActivity(intent);
    }

    public static void toAbout() {
        Intent intent = new Intent(getAppContext(), AboutActivity.class);
        getCurrentActivity().startActivity(intent);
    }

    public static void toSettings() {
        Intent intent = new Intent(getAppContext(), SettingsActivity.class);
        getCurrentActivity().startActivity(intent);
    }

    public static int newId() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    private static int calculateInSampleSize(
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

        Bitmap bm;
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

    private static float getDegree(String exifOrientation) {
        float degree = 0;
        switch (exifOrientation) {
            case "6":
                degree = 90;
                break;
            case "3":
                degree = 180;
                break;
            case "8":
                degree = 270;
        }
        return degree;
    }

    private static Bitmap createRotatedBitmap(Bitmap bm, float degree) {
        Matrix matrix = new Matrix();
        matrix.preRotate(degree);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }

    public static boolean setPic(String mCurrentPhotoPath, ImageView view) {
        //view еще не размещен, но тогда не будут вставляться в View у которых не используется getViewTreeObserver
//        if (view.getWidth() == 0)
//            return true;

        int targetW = view.getWidth();
        if (targetW == 0) targetW = 100;
        int targetH = view.getHeight();
        if (targetH == 0) targetH = 100;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW;
        int photoH;
        float degree;
        try {
            ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
            String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            degree = getDegree(exifOrientation);
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            if (degree == 90 || degree == 270) {
                photoW = bmOptions.outHeight;
                photoH = bmOptions.outWidth;
            } else {
                photoW = bmOptions.outWidth;
                photoH = bmOptions.outHeight;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        final int scaleFactor = Math.min(Math.round((float) photoW / targetW), Math.round((float) photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        if (bitmap != null) {
            if (degree != 0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, photoH / scaleFactor, photoW / scaleFactor, false);
                bitmap = createRotatedBitmap(bitmap, degree);
            } else {
                bitmap = Bitmap.createScaledBitmap(bitmap, photoW / scaleFactor, photoH / scaleFactor, false);
            }
            if (bitmap != null) {
                view.setImageBitmap(bitmap);
                //view.setLayoutParams(new LinearLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
                return true;
            }
        }
        return false;
    }

    /*
        public static boolean fileMove(String src, String dst) throws IOException {
            int pos1 = dst.lastIndexOf("/");
            String res = dst.substring(0, pos1 + 1);
            File dir = new File(dst.substring(0, pos1 + 1));
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    return false;
                }
            }
            try {
                fileCopy(new File(src), new File(dst));
                return fileDelete(src);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    */
    public static void fileCopy(File src, File dst) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(dst));

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
        String file = Preferences.getSyncFolder() + File.separator + newFileName + ".jpg";
        File newfile = new File(file);
        newfile.createNewFile();
        return Uri.fromFile(newfile);
    }

    public static boolean fileDelete(String name) {
        File file = new File(name);
        return file.delete();
    }

    private static List<String> getAllFiles() {
        List<String> files = new ArrayList<>();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            String mainPhoto = recipe.getPhoto();
            if (mainPhoto != null && mainPhoto.length() > 0) {
                files.add(mainPhoto);
            }
            List<Recipe.Step> stepsList = recipe.getStepList();
            for (int j = 0; j < stepsList.size(); j++) {
                Recipe.Step step = stepsList.get(j);
                if (step.fileName != null && step.fileName.length() > 0)
                    files.add(step.fileName);
            }
        }
        return files;
    }

    /*
        public static boolean exportPhotos() {
            for (int i = 0; i < recipes.size(); i++) {
                Recipe recipe = recipes.get(i);
                String mainPhoto = recipe.getPhoto();
                if (mainPhoto != null && mainPhoto.length() > 0) {
                    String newMainPhoto = mainPhoto.replace("Ladle", exportFolderName);
    //                try {
    //                    fileMove(mainPhoto, newMainPhoto);
                    recipe.setPhoto(newMainPhoto);
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                    return false;
    //                }
                }
                List<Recipe.Step> stepsList = recipe.getStepList();
                for (int j = 0; j < stepsList.size(); j++) {
                    Recipe.Step step = stepsList.get(j);
                    if (step.fileName != null && step.fileName.length() > 0) {
                        String newStepPhoto = step.fileName.replace("Ladle", exportFolderName);
    //                    try {
    //                        fileMove(step.fileName, newStepPhoto);
                        step.fileName = newStepPhoto;
    //                    } catch (IOException e) {
    //                        e.printStackTrace();
    //                        return false;
    //                    }
                    }
                }
            }
            return true;
        }
    */
    public static boolean deletePhotos() {
        boolean res = true;
        List<String> files = getAllFiles();
        File[] filesOnFolder = new File[0];
        try {
            filesOnFolder = new File(Preferences.getSyncFolder()).listFiles();
            for (File aFilesOnFolder : filesOnFolder) {
                String fileName = aFilesOnFolder.getAbsolutePath();
                if (fileName.contains(".jpg")) {
                    if (files.indexOf(fileName) == -1)
                        res = fileDelete(fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<String> getExternalMounts() {
        final List<String> out = new ArrayList<>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }

    private String getInternalDirectoryPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private String getSDcardDirectoryPath() {
        return System.getenv("SECONDARY_STORAGE");
    }


    public static List<String> getSdCards() {
        List<String> sVold = new ArrayList<>();
        //sVold.add("/mnt/sdcard");
        try {
            Scanner scanner = new Scanner(new File("/system/etc/vold.fstab"));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("dev_mount")) {
                    String[] lineElements = line.split(" ");
                    String element = lineElements[2];

                    if (element.contains(":"))
                        element = element.substring(0, element.indexOf(":"));

                    if (element.contains("usb"))
                        continue;

                    // don't add the default vold path
                    // it's already in the list.
                    if (!sVold.contains(element))
                        sVold.add(element);
                }
            }
        } catch (Exception e) {
            // swallow - don't care
            e.printStackTrace();
        }
        return sVold;
    }

    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

    /**
     * Raturns all available SD-Cards in the system (include emulated)
     * <p>
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standart way to get it.
     * TODO: Test on future Android versions 4.4+
     *
     * @return paths to all available SD-Cards in the system (include emulated)
     */
    public static String[] getStorageDirectories() {
        // Final set of paths
        final Set<String> rv = new HashSet<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    //Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }

    public static File getExistsFolder(String path) {
        if (path.isEmpty())
            throw new IllegalArgumentException();
        File folder = new File(path);
        if (folder.exists())
            return folder;
        else {
            if (folder.mkdirs()) {
                return folder;
            } else {
                throw new SecurityException(path);
            }
        }
    }

    private static boolean permissionGranted() {
        if (Build.VERSION.SDK_INT < 23) return true;
        if (ContextCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        if (!permissionRequested) {
            MyApp.getCurrentActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MyApp.SDCARD_PERMISSION);
            permissionRequested = true;
        }
        return ContextCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
