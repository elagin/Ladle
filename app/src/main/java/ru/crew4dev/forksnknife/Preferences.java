package ru.crew4dev.forksnknife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by elagin on 10.01.17.
 */
@SuppressLint("CommitPrefEdits")
public class Preferences {

    public final static  String syncFolder;

    private static SharedPreferences preferences;

    static {
        syncFolder = "sync_folder";
    }

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getSyncFolder() {
        return preferences.getString(syncFolder, "");
    }

    public static void setSyncFolder(String value) {
        preferences.edit().putString(syncFolder, value);
    }
}
