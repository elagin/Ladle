package com.pavel.elagin.ladle.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pavel.elagin.ladle.MyApp;
import com.pavel.elagin.ladle.R;
import com.pavel.elagin.ladle.Recipe;

import java.util.List;

public class ViewRecActivity extends AppCompatActivity {

    private TextView rec_name;
    private TextView rec_descr;
    private TableLayout table;
    private TextView rec_total_time_count;
    private TextView rec_steps;

    private Integer recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rec);

        table = (TableLayout) findViewById(R.id.table_rec_ing_view);
        table.requestLayout();     // Not sure if this is needed.

        rec_name = (TextView) findViewById(R.id.rec_name);
        rec_descr = (TextView) findViewById(R.id.rec_descr);
        rec_steps = (TextView) findViewById(R.id.rec_steps);
        rec_total_time_count = (TextView) findViewById(R.id.rec_total_time_count);
        rec_steps = (TextView) findViewById(R.id.rec_steps);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeID = bundle.getInt("recipeID");
            update();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_rec_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_rec:
                MyApp.toEdit(recipeID);
                return true;
            case R.id.action_share_rec:
                shareRecipe();
                return true;
        }
        return false;
    }

    private void shareRecipe() {
        Recipe recipe = MyApp.getRecipe(recipeID);
        StringBuffer buffer = new StringBuffer();
        buffer.append(recipe.getDescription());

        Integer totalTime = recipe.getTotalTime();
        if(totalTime != null){
            buffer.append("\n\n");
            buffer.append(getString(R.string.time) + totalTime.toString());
        }
        String steps = recipe.getSteps();
        if(steps.length() > 0){
            buffer.append("\n\n");
            buffer.append(getString(R.string.preparation) + "\n" + steps);
        }

        List<Recipe.Ingredient> ingredientList = recipe.getIngredients();
        buffer.append("\n\n");
        buffer.append(getString(R.string.rec_ingred));
        for (int i = 0; i < ingredientList.size(); i++) {
            Recipe.Ingredient item = ingredientList.get(i);
            buffer.append("\n");
            buffer.append(item.name + " " + item.count + " " + item.unit);
        }
        buffer.append("\n--------\n");
        buffer.append(getString(R.string.about_info));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.recipe) + " " + recipe.getName());
        intent.putExtra(Intent.EXTRA_TEXT, buffer.toString());
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        Recipe recipe;
        if (recipeID != null) {
            recipe = MyApp.getRecipe(recipeID);
            rec_name.setText(recipe.getName());
            rec_descr.setText(recipe.getDescription());
            rec_steps.setText(recipe.getSteps());
            table.removeAllViews();
            List<Recipe.Ingredient> ingredientList = recipe.getIngredients();
            for (int i = 0; i < ingredientList.size(); i++) {
                Recipe.Ingredient item = ingredientList.get(i);
                addIng(item.name, item.count, item.unit);
            }
            rec_total_time_count.setText(String.format(getString(R.string.time_format), recipe.getTotalTime().toString()));
        }
    }

    private void addIng(String name, Double count, String unit) {
        final int index = table.getChildCount();
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.rec_ing_view_row, null);
        ((TextView) row.findViewById(R.id.ing_name)).setText(name);
        ((TextView) row.findViewById(R.id.ing_count)).setText(count.toString().replace(".0", ""));
        ((TextView) row.findViewById(R.id.ing_unit)).setText(unit);
        row.setId(index);
        table.addView(row);
    }
}