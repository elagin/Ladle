package com.pavel.elagin.ladle.Activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.pavel.elagin.ladle.MyApp;
import com.pavel.elagin.ladle.R;
import com.pavel.elagin.ladle.Recipe;

import java.util.List;

public class EditRecActivity extends AppCompatActivity implements View.OnClickListener {

    private TableLayout table;
    private TextView edit_rec_name;
    private TextView edit_rec_descr;
    private TextView edit_rec_total_time_count;
    private TextView edit_rec_steps;
    private Integer recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rec);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        table = (TableLayout) findViewById(R.id.table_rec_ing);
        table.requestLayout();     // Not sure if this is needed.

        ImageButton button_add_ing = (ImageButton) findViewById(R.id.button_add_ing);
        button_add_ing.setOnClickListener(this);

        edit_rec_name = (TextView) findViewById(R.id.edit_rec_name);
        edit_rec_descr = (TextView) findViewById(R.id.edit_rec_descr);
        edit_rec_steps = (TextView) findViewById(R.id.edit_rec_steps);
        edit_rec_total_time_count = (TextView) findViewById(R.id.edit_rec_total_time_count);

        //MyApp.loadRecipesJSon(true);
        Recipe recipe;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeID = bundle.getInt("recipeID");
            if (recipeID != null) {
                recipe = MyApp.getRecipe(recipeID);
                edit_rec_name.setText(recipe.getName());
                edit_rec_descr.setText(recipe.getDescription());
                edit_rec_steps.setText(recipe.getSteps());
                edit_rec_total_time_count.setText(recipe.getTotalTime().toString());
                List<Recipe.Ingredient> ingredientList = recipe.getIngredients();
                for (int i = 0; i < ingredientList.size(); i++) {
                    Recipe.Ingredient item = ingredientList.get(i);
                    addIng(item.name, item.count, item.unit);
                }
            }
        } else
            addIng(null, null, null);
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
                String recipeName = edit_rec_name.getText().toString();
                if (recipeName.length() == 0) {
                    Toast.makeText(this, getString(R.string.rec_name_is_empty), Toast.LENGTH_LONG).show();
                    return false;
                }
                Recipe recipe = new Recipe();
                recipe.setName(recipeName);
                recipe.setDescription(edit_rec_descr.getText().toString());
                recipe.setSteps(edit_rec_steps.getText().toString());
                String totalTime = edit_rec_total_time_count.getText().toString();
                if (totalTime.length() > 0)
                    recipe.setTotalTime(Integer.valueOf(totalTime));
                recipe.clearIngredients();

                for (int i = 0, j = table.getChildCount(); i < j; i++) {
                    View view = table.getChildAt(i);
                    if (view instanceof TableRow) {
                        TableRow row = (TableRow) view;
                        TextView nameView = (TextView) row.findViewById(R.id.ing_name);
                        TextView countView = (TextView) row.findViewById(R.id.ing_count);
                        TextView unitView = (TextView) row.findViewById(R.id.ing_unit);

                        String name = nameView.getText().toString();
                        String count = countView.getText().toString();
                        String unit = unitView.getText().toString();

                        if (name.length() == 0) {
                            Toast.makeText(this, getString(R.string.rec_name_ing_is_empty), Toast.LENGTH_LONG).show();
                            return false;
                        }
                        if (count.length() == 0) {
                            Toast.makeText(this, getString(R.string.rec_volume_ing_is_empty), Toast.LENGTH_LONG).show();
                            return false;
                        }
                        if (unit.length() == 0) {
                            Toast.makeText(this, getString(R.string.rec_unit_ing_is_empty), Toast.LENGTH_LONG).show();
                            return false;
                        }
                        recipe.addIngredient(name, Double.valueOf(count), unit);
                    }
                }
                if (recipeID == null)
                    recipe.setUid(MyApp.newId());
                else
                    recipe.setUid(recipeID);

                MyApp.updateRecipe(recipe);
                MyApp.saveRecipesJSon(true);
                finish();
                return true;
        }
        return false;
    }

    private void addIng(String name, Double count, String unit) {
        final int index = table.getChildCount();

        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.rec_ing_row, null);
        if (name != null)
            ((TextView) row.findViewById(R.id.ing_name)).setText(name);
        if (count != null)
            ((TextView) row.findViewById(R.id.ing_count)).setText(count.toString());
        if (unit != null)
            ((TextView) row.findViewById(R.id.ing_unit)).setText(unit);
        row.setId(index);
        table.addView(row);

        ImageButton btn = (ImageButton) row.findViewById(R.id.dell_ing);
        btn.setId(index);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0, j = table.getChildCount(); i < j; i++) {
                    View row = table.getChildAt(i);
                    if (row.getId() == v.getId()) {
                        table.removeViewAt(i);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_add_ing:
                addIng(null, null, null);
                break;
        }
    }
}
