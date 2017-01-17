package ru.crew4dev.forksnknife.Activites;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.Preferences;
import ru.crew4dev.forksnknife.R;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class AboutActivity extends AppCompatActivity {

    private TextView about_tech_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            about_logo_rights.setText(Html.fromHtml(getString(R.string.about_thanks)));
        else
            about_logo_rights.setText(Html.fromHtml(getString(R.string.about_thanks), FROM_HTML_MODE_COMPACT));
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
        StringBuilder msg = new StringBuilder();
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
        msg.append(android.os.Build.MODEL).append(" ").append(Build.VERSION.RELEASE).append("\n");

        msg.append("Данные: ");
        try {
            msg.append(Preferences.getSyncFolder());
            msg.append("\n");
        } catch (Exception e) {
            msg.append(e.getLocalizedMessage());
            msg.append("\n");
        }

        File internal = MyApp.getInternalStorage();
        if (internal != null) {
            msg.append("Внутреняя память: ");
            int pos = internal.getAbsolutePath().lastIndexOf("/");
            msg.append(internal.getAbsolutePath().substring(0, pos));
            msg.append("\n");
        }
        File external = MyApp.getExternalStorage();
        if (external != null) {
            msg.append("Внешняя память: ");
            int pos = external.getAbsolutePath().lastIndexOf("/");
            msg.append(external.getAbsolutePath().substring(0, pos));
            msg.append("\n");
        }
        List<String> mounts = MyApp.getExternalMounts();
        if (!mounts.isEmpty()) {
            msg.append("Монтированные: ");
            for (String item : mounts) {
                msg.append(item);
                msg.append("\n");
            }
        }

//        String androidStorageName = "ANDROID_STORAGE";
//        if (androidStorageName != null && androidStorageName.length() > 0) {
//            String androidStorage = System.getenv(androidStorageName);
//            File storages = new File(androidStorage);
//            String[] storeFolders = storages.list();
//            for (String storeFolder : storeFolders) {
//                msg.append(androidStorage).append(File.separator).append(storeFolder).append("\n");
//            }
//        }
        about_tech_info.setText(msg);
    }
}
