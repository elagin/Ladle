package com.pavel.elagin.ladle.Activites;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.pavel.elagin.ladle.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView about_feedback = (TextView) findViewById(R.id.about_feedback);
        about_feedback.setText(Html.fromHtml(getString(R.string.feedback)));
        about_feedback.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
