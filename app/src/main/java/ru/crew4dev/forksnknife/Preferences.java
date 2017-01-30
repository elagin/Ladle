package ru.crew4dev.forksnknife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

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

    public static String getSyncFolder(Context context) throws IllegalArgumentException, SecurityException {
        String path = preferences.getString(syncFolder, "");
        if (!path.isEmpty())
            return path;
        else {
            try {
                File externalStorage = MyApp.getExternalStorage();
                if (externalStorage != null && externalStorage.canWrite()) {
                    path = externalStorage.getAbsolutePath();
                } else {
                    File internalStorage = Environment.getExternalStorageDirectory();
                    if (internalStorage.canWrite())
                        path = internalStorage.getAbsolutePath();
                    else {
                        path = MyApp.getFileDir();
                        if (MyApp.getExistsFolder(path, false) != null) {
                            setSyncFolder(path);
                            return path;
                        }
                    }
                }
                File folder = MyApp.getExistsFolder(path, true);
                setSyncFolder(folder.getAbsolutePath());
                return folder.getAbsolutePath();
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(ex);
            } catch (Exception e) {
                Toast.makeText(context, String.format(context.getString(R.string.error_find_storages), e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

    public static void setSyncFolder(String value) {
        preferences.edit().putString(syncFolder, value).commit();
    }
}
