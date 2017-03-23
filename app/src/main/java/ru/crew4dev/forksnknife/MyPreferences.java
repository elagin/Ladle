package ru.crew4dev.forksnknife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by elagin on 23.03.17.
 */

@SuppressLint("CommitPrefEdits")
public class MyPreferences {

    private static SharedPreferences preferences;
    private static Context context;

    private static final String serverURI = "server";

    public MyPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        MyPreferences.context = context;
    }

    public URL getServerURI() {
        //http://crew4dev.ru/forksnknife/request.php?m=list
        String DefaultURI = "http://crew4dev.ru/forksnknife/request.php";
        String URI = preferences.getString(serverURI, "");
        if (URI.equals("")) {
            setServerURI(DefaultURI);
            URI = DefaultURI;
        }
        try {
            return new URL(URI);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setServerURI(String URI) {
        preferences.edit().putString(serverURI, URI);
    }
}
