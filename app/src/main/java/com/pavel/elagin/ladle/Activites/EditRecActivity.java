package com.pavel.elagin.ladle.Activites;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pavel.elagin.ladle.R;

public class EditRecActivity extends AppCompatActivity implements View.OnClickListener {

    Button button_add_ing;
    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rec);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        table = (TableLayout) findViewById(R.id.table_rec_ing);
        table.requestLayout();     // Not sure if this is needed.

        button_add_ing = (Button) findViewById(R.id.button_add_ing);
        button_add_ing.setOnClickListener(this);
//        button_add_ing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO Добавить запрос подтверждения на выход.
////                AccidentsGeneral.auth.logoff();
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_rec_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_rec:
//				Intent intentAbout = new Intent(getActivity(), AboutActivity.class);
//				this.startActivity(intentAbout);
                return true;
        }
        return false;
    }

    private void addIng(String name, Integer count) {
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.rec_ing_row, null);
        ((TextView) row.findViewById(R.id.ing_name)).setText(name);
        ((TextView) row.findViewById(R.id.ing_count)).setText(count.toString());
        table.addView(row);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_add_ing:
                addIng("Оливки", 10);
                //startActivity(new Intent(this, VKLoginActivity.class));
                //VKSdk.authorize(sMyScope, true, true);
                break;
        }
    }
}
