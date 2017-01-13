package ru.crew4dev.forksnknife.Activites;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.R;

import java.io.File;

public class AboutActivity extends AppCompatActivity {

    TextView about_tech_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        PackageManager manager = MyApp.getAppContext().getPackageManager();
        String version;
        try {
            PackageInfo info = manager.getPackageInfo(MyApp.getAppContext().getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = MyApp.getAppContext().getString(R.string.unknown_code_version);
        }
        ((TextView) this.findViewById(R.id.app_info)).setText(getString(R.string.code_version_prefix) + ": " + version);

        TextView about_logo_rights = (TextView) findViewById(R.id.about_thanks);
        about_logo_rights.setText(Html.fromHtml(getString(R.string.about_thanks)));
        about_logo_rights.setMovementMethod(LinkMovementMethod.getInstance());

        about_tech_info = (TextView) findViewById(R.id.about_tech_info);

        Button button_tech_info = (Button) findViewById(R.id.button_tech_info);
        button_tech_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (about_tech_info.getVisibility() == View.VISIBLE)
                    about_tech_info.setVisibility(View.GONE);
                else
                    about_tech_info.setVisibility(View.VISIBLE);
            }
        });
        MyApp.getStorageDirectories();
        //MyApp.getSdCards();
        String msg = "";
/*
        msg += "System properties\n";
        msg += "-------------\n";
        java.util.Properties props = System.getProperties();
        java.util.Enumeration e = props.propertyNames();
        while (e.hasMoreElements()) {
            String k = (String) e.nextElement();
            String v = props.getProperty(k);
            msg += k + ": " + v + "\n";
        }

        msg += "\n";
        msg += "Envirionment variables\n";
        msg += "-------------\n";
        java.util.Map envs = System.getenv();
        java.util.Set keys = envs.keySet();
        java.util.Iterator i = keys.iterator();
        while (i.hasNext()) {
            String k = (String) i.next();
            String v = (String) envs.get(k);
            msg += k + ": " + v + "\n";
        }
*/
        msg += android.os.Build.MODEL + " " + Build.VERSION.RELEASE + "\n";
        String androidStorageName = "ANDROID_STORAGE";
        if (androidStorageName != null && androidStorageName.length() > 0) {
            String androidStorage = System.getenv(androidStorageName);
            File storages = new File(androidStorage);
            String[] storeFolders = storages.list();
            for (int x = 0; x < storeFolders.length; x++) {
                msg += androidStorage + File.separator + storeFolders[x] + "\n";
            }
        }
        about_tech_info.setText(msg);
    }
}
