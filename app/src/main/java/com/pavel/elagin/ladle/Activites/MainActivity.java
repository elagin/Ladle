package com.pavel.elagin.ladle.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pavel.elagin.ladle.MyApp;
import com.pavel.elagin.ladle.R;
import com.pavel.elagin.ladle.Recipe;

import java.util.List;

import static com.pavel.elagin.ladle.MyApp.getAppContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp.setCurrentActivity(this);

        table = (TableLayout) findViewById(R.id.main_rec_table);
        table.requestLayout();     // Not sure if this is needed.

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        MyApp.loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
            fillTable();
    }

    private void fillTable() {
        table.removeAllViews();
        final List<Recipe> recipes = MyApp.getRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe item = recipes.get(i);
            try {
                TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.recipe_row, null);
                ((TextView) row.findViewById(R.id.rec_name_row)).setText(item.getName() + " " + item.getDescription());
                final int rowId = MyApp.newId();
                row.setId(rowId);
                row.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        MyApp.toDetails(v.getId());
                    }
                });
                table.addView(row);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab:
                Intent intent = new Intent(getAppContext(), EditRecActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt("recipeID", id);
//        intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
