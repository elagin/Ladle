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
        String folder;
        folder = preferences.getString(syncFolder, "");
        if (folder.length() > 0)
            return folder;
        else {
            File externalStorage = MyApp.getExternalStorage();
            if (externalStorage != null) {
                folder = externalStorage.getAbsolutePath();
            } else {
                File internalStorage = MyApp.getInternalStorage();
                if (internalStorage != null)
                    folder = internalStorage.getAbsolutePath();
                else {
                    folder = MyApp.getFileDir();
                }
            }
        }
        setSyncFolder(folder);
        return folder;
    }

    public static void setSyncFolder(String value) {
        preferences.edit().putString(syncFolder, value);
    }
}
