package ru.crew4dev.forksnknife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;

/**
 * Created by elagin on 10.01.17.
 */
@SuppressLint("CommitPrefEdits")
public class Preferences {

    private final static String syncFolder;

    private static SharedPreferences preferences;

    static {
        syncFolder = "sync_folder";
    }

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getSyncFolder() {
        String path = preferences.getString(syncFolder, "");
        if (path.isEmpty()) {
            File externalStorage = MyApp.getExternalStorage();
            if (externalStorage != null) {
                path = externalStorage.getAbsolutePath();
            } else {
                File internalStorage = MyApp.getInternalStorage();
                if (internalStorage != null)
                    path = internalStorage.getAbsolutePath();
                else {
                    path = MyApp.getFileDir();
                }
            }
            File folder = MyApp.getExistsFolder(path);
            if (folder != null)
                setSyncFolder(path);
            return folder.getAbsolutePath();
        }
        return path;
    }

    public static void setSyncFolder(String value) {
        preferences.edit().putString(syncFolder, value).commit();
    }
}
