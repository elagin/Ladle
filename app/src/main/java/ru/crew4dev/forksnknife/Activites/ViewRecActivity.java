package ru.crew4dev.forksnknife.Activites;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.Preferences;
import ru.crew4dev.forksnknife.R;
import ru.crew4dev.forksnknife.Recipe;

public class ViewRecActivity extends AppCompatActivity {

    private TextView rec_name;
    private TextView rec_descr;
    private TextView rec_tags;
    private TextView rec_tags_label;
    private TableLayout recTable;
    private TableLayout stepTable;
    private TextView rec_total_time_count;
    private TextView rec_steps;
    private ImageView image_main;

    private Integer recipeID;
    private static File shareFile;
    private static final int RESULT_SHARE_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rec);

        recTable = (TableLayout) findViewById(R.id.table_rec_ing_view);
        recTable.requestLayout();     // Not sure if this is needed.

        image_main = (ImageView) findViewById(R.id.image_main);
        image_main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    image_main.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    image_main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                update();
            }
        });

        stepTable = (TableLayout) findViewById(R.id.table_rec_steps_view);
        stepTable.requestLayout();     // Not sure if this is needed.

        rec_name = (TextView) findViewById(R.id.rec_name);
        rec_descr = (TextView) findViewById(R.id.rec_descr);
        rec_tags = (TextView) findViewById(R.id.rec_tags);
        rec_tags_label = (TextView) findViewById(R.id.rec_tags_label);
        rec_steps = (TextView) findViewById(R.id.rec_steps);
        rec_total_time_count = (TextView) findViewById(R.id.rec_total_time_count);

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
            case R.id.action_share_rec_as_text:
                shareRecipe();
                return true;
            case R.id.action_share_rec_as_file:
                saveRecipe(this, MyApp.getRecipe(recipeID));
                return true;
            case R.id.action_coocking_rec:
                MyApp.toCoocking(recipeID);
                return true;
        }
        return false;
    }

    private void shareRecipe() {
        Recipe recipe = MyApp.getRecipe(recipeID);
        if (recipe == null) {
            Toast.makeText(this, getString(R.string.error_recipe_is_not_found), Toast.LENGTH_LONG).show();
            return;
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append(recipe.getName());
        Integer totalTime = recipe.getTotalTime();
        if (totalTime != null) {
            buffer.append(" - ");
            buffer.append(getString(R.string.time_format_w_mins, totalTime.toString()));
        }

        if (recipe.getDescription() != null && recipe.getDescription().length() > 0) {
            buffer.append("\n");
            buffer.append(recipe.getDescription());
        }

        List<Recipe.Ingredient> ingredientList = recipe.getIngredients();
        buffer.append("\n");
        buffer.append(getString(R.string.rec_ingred));
        for (int i = 0; i < ingredientList.size(); i++) {
            Recipe.Ingredient item = ingredientList.get(i);
            buffer.append("\n");
            buffer.append(item.name).append(" ").append(item.count).append(" ").append(item.unit);
        }

        List<Recipe.Step> stepList = recipe.getStepList();
        if (stepList.size() > 0) {
            int stepNumber = 1;
            buffer.append("\n\n");
            buffer.append(getString(R.string.preparation)).append("\n");
            for (Recipe.Step step : stepList) {
                buffer.append(stepNumber).append(") ");
                if (step.time != null && step.time > 0) {
                    buffer.append(String.format(getString(R.string.time_format_w_mins), step.time.toString()));
                    buffer.append(" - ");
                }
                buffer.append(step.desc);
                buffer.append("\n");
                stepNumber++;
            }
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
        if (recipeID != null) {
            Recipe recipe = MyApp.getRecipe(recipeID);

            image_main = (ImageView) findViewById(R.id.image_main);
            if (recipe.getPhoto() != null && recipe.getPhoto().length() > 0) {
                MyApp.setPic(recipe.getPhoto(), image_main);
            } else
                image_main.setVisibility(View.GONE);

            rec_name.setText(recipe.getName());
            if (recipe.getDescription().length() > 0)
                rec_descr.setText(recipe.getDescription());
            else
                rec_descr.setVisibility(View.GONE);
            if (recipe.getTags().length() > 0) {
                rec_tags.setText(recipe.getTags());
            } else {
                rec_tags_label.setVisibility(View.GONE);
                rec_tags.setVisibility(View.GONE);
            }
            if (recipe.getSteps() != null && recipe.getSteps().length() > 0)
                rec_steps.setText(recipe.getSteps());
            else
                rec_steps.setVisibility(View.GONE);
            recTable.removeAllViews();
            List<Recipe.Ingredient> ingredientList = recipe.getIngredients();
            for (int i = 0; i < ingredientList.size(); i++) {
                Recipe.Ingredient item = ingredientList.get(i);
                addIng(item.name, item.count, item.unit);
            }

            stepTable.removeAllViews();
            List<Recipe.Step> stepList = recipe.getStepList();
            for (int i = 0; i < stepList.size(); i++) {
                Recipe.Step item = stepList.get(i);
                addStep(item.fileName, item.time, item.desc);
            }
            if (stepList.isEmpty())
                findViewById(R.id.rec_preparation).setVisibility(View.GONE);

            String totalTime = recipe.getTotalStepTimeString();
            if (totalTime.length() > 0)
                rec_total_time_count.setText(String.format(getString(R.string.time_format_w_mins), totalTime));
            else
                rec_total_time_count.setVisibility(View.GONE);
        }
    }

    private void addIng(String name, Double count, String unit) {
        final int index = recTable.getChildCount();
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.rec_ing_view_row, null);
        ((TextView) row.findViewById(R.id.ing_name)).setText(name);
        ((TextView) row.findViewById(R.id.ing_count)).setText(count.toString().replace(".0", ""));
        ((TextView) row.findViewById(R.id.ing_unit)).setText(unit);
        row.setId(index);
        recTable.addView(row);
    }

    private void addStep(String fileName, Integer time, String descr) {
        final int index = stepTable.getChildCount();
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.step_view_row, null);
        TextView edit_step_time = (TextView) row.findViewById(R.id.coocking_step_time);
        if (time != null && time > 0)
            edit_step_time.setText(String.format(getString(R.string.time_format_w_mins), time.toString()));
        else
            edit_step_time.setVisibility(View.GONE);
        ((TextView) row.findViewById(R.id.coocking_step_descr)).setText(descr);

        Integer stepNumber = index + 1;
        ((TextView) row.findViewById(R.id.coocking_step_number)).setText(stepNumber.toString());
        if (fileName != null) {
            Bitmap bm = MyApp.decodeSampledBitmapFromUri(fileName, 100, 100);
            ((ImageView) row.findViewById(R.id.coocking_step_photo)).setImageBitmap(bm);
        } else {
            (row.findViewById(R.id.coocking_step_photo)).setVisibility(View.GONE);
        }
        row.setId(index);
        stepTable.addView(row);
    }

    public void saveRecipe(Context context, Recipe recipe) {
        Long timestamp = System.currentTimeMillis();
        String newFileName = timestamp.toString();
        StringBuilder path = new StringBuilder();
        shareFile = new File(path.append(Preferences.getSyncFolder()).append(File.separator).append(newFileName).append(".fnk").toString());
        if (MyApp.saveRecipeJSon(context, shareFile.getAbsolutePath(), recipe)) {
            MyApp.email(this, path.toString(), recipe.getName(), RESULT_SHARE_FILE);
            shareFile.deleteOnExit();
            if (!shareFile.exists())
                shareFile = null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_SHARE_FILE:
                try {
                    // if shareFile exists in memory
                    if (shareFile != null && shareFile.exists()) {
                        shareFile.delete();
                        shareFile = null;
                    }
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }
}