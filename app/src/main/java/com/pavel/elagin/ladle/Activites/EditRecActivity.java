package com.pavel.elagin.ladle.Activites;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.pavel.elagin.ladle.MyApp;
import com.pavel.elagin.ladle.R;
import com.pavel.elagin.ladle.Recipe;

import java.io.File;
import java.util.List;

import static com.pavel.elagin.ladle.MyApp.getAppContext;

public class EditRecActivity extends AppCompatActivity implements View.OnClickListener {

    private TableLayout table;
    private TableLayout edit_rec_steps_table;
    private TextView edit_rec_name;
    private TextView edit_rec_descr;
    private TextView edit_rec_total_time_count;
    private TextView edit_rec_steps;
    private Integer recipeID;

    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rec);

        table = (TableLayout) findViewById(R.id.table_rec_ing);
        table.requestLayout();     // Not sure if this is needed.

        edit_rec_steps_table = (TableLayout) findViewById(R.id.edit_rec_steps_table);
        edit_rec_steps_table.requestLayout();     // Not sure if this is needed.

        ImageButton button_add_ing = (ImageButton) findViewById(R.id.button_add_ing);
        button_add_ing.setOnClickListener(this);

        edit_rec_name = (TextView) findViewById(R.id.edit_rec_name);
        edit_rec_descr = (TextView) findViewById(R.id.edit_rec_descr);
        edit_rec_steps = (TextView) findViewById(R.id.edit_rec_steps);
        edit_rec_total_time_count = (TextView) findViewById(R.id.edit_rec_total_time_count);

        ImageButton button_add_step = (ImageButton) findViewById(R.id.button_add_step);
        button_add_step.setOnClickListener(this);

        //MyApp.loadRecipesJSon(true);
        Recipe recipe;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeID = bundle.getInt("recipeID");
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
            List<Recipe.Step> stepList = recipe.getStepList();
            for (int i = 0; i < stepList.size(); i++) {
                Recipe.Step item = stepList.get(i);
                addStep(item);
            }
        } else {
            addIng(null, null, null);
            addStep(null);
        }
//        load();
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

                for (int i = 0, j = edit_rec_steps_table.getChildCount(); i < j; i++) {
                    View view = edit_rec_steps_table.getChildAt(i);
                    if (view instanceof TableRow) {
                        TableRow row = (TableRow) view;
                        TextView edit_step_time = (TextView) row.findViewById(R.id.edit_step_time);
                        TextView edit_step_descr = (TextView) row.findViewById(R.id.edit_step_descr);
                        TextView text_photo_url = (TextView) row.findViewById(R.id.text_photo_url);

                        String descr = edit_step_descr.getText().toString();
                        String time =  edit_step_time.getText().toString();
                        String photoUrl = text_photo_url.getText().toString();
                        recipe.addStep(photoUrl, descr, Integer.valueOf(time));
                    }
                }

                if (recipeID == null)
                    recipe.setUid(MyApp.newId());
                else
                    recipe.setUid(recipeID);

                MyApp.updateRecipe(recipe);
                MyApp.saveRecipesJSon(this, true);
                finish();
                return true;
            case R.id.action_cancel_rec:
                finish();
                return true;
        }
        return false;
    }

    private void addStep(Recipe.Step step) {
        final int index = edit_rec_steps_table.getChildCount();
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.prepare_step_row, null);
        ImageButton image_view_step = (ImageButton) row.findViewById(R.id.step_photo);
        ImageButton dell_step = (ImageButton) row.findViewById(R.id.dell_step);
        if(step != null) {
            if(step.fileName != null) {
                TextView text_photo_url = (TextView) row.findViewById(R.id.text_photo_url);
                text_photo_url.setText(step.fileName);
                Bitmap bm = decodeSampledBitmapFromUri(step.fileName, 100, 100);
                //Bitmap bm = decodeSampledBitmapFromUri(file.getAbsolutePath(), 200, 200);
                image_view_step.setImageBitmap(bm);
            }
            TextView edit_step_descr = (TextView) row.findViewById(R.id.edit_step_descr);
            edit_step_descr.setText(step.desc);
            if(step.time != null) {
                TextView edit_step_time = (TextView) row.findViewById(R.id.edit_step_time);
                edit_step_time.setText(step.time.toString());
            }
        }
        row.setId(index);
        edit_rec_steps_table.addView(row);
        image_view_step.setId(index);

        dell_step.setId(index);
        dell_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0, j = edit_rec_steps_table.getChildCount(); i < j; i++) {
                    View row = edit_rec_steps_table.getChildAt(i);
                    if (row.getId() == v.getId()) {
                        edit_rec_steps_table.removeViewAt(i);
                        break;
                    }
                }
            }
        });
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
            case R.id.button_add_step:
                addStep(null);
                break;
        }
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        try {
            bm = BitmapFactory.decodeFile(path, options);
            return bm;
        } catch (OutOfMemoryError e) {
            try {
                //options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                return bitmap;
            } catch (Exception ex) {
                Log.d(TAG, String.valueOf(ex));
            }
        }
        return null;
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }


    public boolean load() {
        String targetPath = "/storage/sdcard1/Фото";
        File targetDirector = new File(targetPath);
        //File targetDirector = getAppContext().getFilesDir();
        File[] files = targetDirector.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().contains(".jpg")) {
                Bitmap bm = decodeSampledBitmapFromUri(file.getAbsolutePath(), 200, 200);
//                image_view_step.setImageBitmap(bm);
//                break;
            }
            //image_view_step.add(file.getAbsolutePath());
        }
        return true;
    }
}
