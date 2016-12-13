package com.pavel.elagin.ladle.Activites;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.pavel.elagin.ladle.MyApp;
import com.pavel.elagin.ladle.R;
import com.pavel.elagin.ladle.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.pavel.elagin.ladle.MyApp.decodeSampledBitmapFromUri;

public class EditRecActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "myLogs";
    static final int RESULT_GALLERY_MAIN_IMAGE = 1;
    static final int RESULT_CAM_MAIN_IMAGE = 2;

    private TableLayout table;
    private TableLayout edit_rec_steps_table;
    private TextView edit_rec_name;
    private TextView edit_rec_descr;
    private TextView edit_rec_total_time_count;
    private TextView edit_rec_steps;
    private ImageButton image_main;
    private TextView text_image_main_file;
    private Uri tempCamFileName;
    private Integer recipeID;

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

        image_main = (ImageButton) findViewById(R.id.image_main);
        image_main.setOnClickListener(viewClickListener);

        text_image_main_file = (TextView) findViewById(R.id.text_image_main_file);

        edit_rec_name = (TextView) findViewById(R.id.edit_rec_name);
        edit_rec_descr = (TextView) findViewById(R.id.edit_rec_descr);
        edit_rec_steps = (TextView) findViewById(R.id.edit_rec_steps);
        edit_rec_total_time_count = (TextView) findViewById(R.id.edit_rec_total_time_count);

        ImageButton button_add_step = (ImageButton) findViewById(R.id.button_add_step);
        button_add_step.setOnClickListener(this);
//        Button button_get_photo = (Button) findViewById(R.id.button_get_photo);
//        button_get_photo.setOnClickListener(this);

        Button button_get_photo_cam = (Button) findViewById(R.id.button_get_photo_cam);
        button_get_photo_cam.setOnClickListener(this);

        //dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        //dir = getAppContext().getFilesDir().getAbsolutePath();

        //MyApp.loadRecipesJSon(true);
        Recipe recipe;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeID = bundle.getInt("recipeID");
            recipe = MyApp.getRecipe(recipeID);

            if (recipe.getPhoto() != null && recipe.getPhoto().length() > 0) {
                MyApp.setPic(recipe.getPhoto(), image_main);
                text_image_main_file.setText(recipe.getPhoto());
            }
//            else {
//                image_main.setVisibility(View.GONE);
//            }

            edit_rec_name.setText(recipe.getName());
            edit_rec_descr.setText(recipe.getDescription());
            if (recipe.getSteps().length() > 0)
                edit_rec_steps.setText(recipe.getSteps());
            else
                edit_rec_steps.setVisibility(View.GONE);
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
            edit_rec_steps.setVisibility(View.GONE);
        }
//        load();
    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_rec_menu, menu);
        return true;
    }

    private boolean saveRecipe(String recipeName) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeName);
        recipe.setDescription(edit_rec_descr.getText().toString());
        recipe.setSteps(edit_rec_steps.getText().toString());
        String totalTime = edit_rec_total_time_count.getText().toString();
        if (totalTime.length() > 0)
            recipe.setTotalTime(Integer.valueOf(totalTime));

        for (int i = 0, j = table.getChildCount(); i < j; i++) {
            View view = table.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                String name = ((TextView) row.findViewById(R.id.ing_name)).getText().toString();
                if (name.length() == 0) {
                    Toast.makeText(this, getString(R.string.rec_name_ing_is_empty), Toast.LENGTH_LONG).show();
                    return false;
                }
                String count = ((TextView) row.findViewById(R.id.ing_count)).getText().toString();
                if (count.length() == 0) {
                    Toast.makeText(this, getString(R.string.rec_volume_ing_is_empty), Toast.LENGTH_LONG).show();
                    return false;
                }
                String unit = ((TextView) row.findViewById(R.id.ing_unit)).getText().toString();
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
                String time = ((TextView) row.findViewById(R.id.edit_step_time)).getText().toString();
                String descr = ((TextView) row.findViewById(R.id.edit_step_descr)).getText().toString();
                String photoUrl = ((TextView) row.findViewById(R.id.text_photo_url)).getText().toString();
                recipe.addStep(photoUrl, descr, Integer.valueOf(time));
            }
        }

        String imageFile = text_image_main_file.getText().toString();
        if (imageFile.length() > 0)
            recipe.setPhoto(imageFile);

        if (recipeID == null)
            recipe.setUid(MyApp.newId());
        else
            recipe.setUid(recipeID);

        MyApp.updateRecipe(recipe);
        MyApp.saveRecipesJSon(this, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_rec:
                String recipeName = edit_rec_name.getText().toString();
                if (recipeName.length() > 0) {
                    if(saveRecipe(recipeName)) {
                        finish();
                        return true;
                    }
                } else {
                    Toast.makeText(this, getString(R.string.rec_name_is_empty), Toast.LENGTH_LONG).show();
                    return false;
                }
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
        if (step != null) {
            if (step.fileName != null) {
                TextView text_photo_url = (TextView) row.findViewById(R.id.text_photo_url);
                text_photo_url.setText(step.fileName);
                Bitmap bm = MyApp.decodeSampledBitmapFromUri(step.fileName, 100, 100);
                //Bitmap bm = decodeSampledBitmapFromUri(file.getAbsolutePath(), 200, 200);
                image_view_step.setImageBitmap(bm);
            }
            TextView edit_step_descr = (TextView) row.findViewById(R.id.edit_step_descr);
            edit_step_descr.setText(step.desc);
            if (step.time != null) {
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
                        Recipe recipe = MyApp.getRecipe(recipeID);
                        recipe.deleteStep(v.getId());
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

    private void setMainPhotoFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_GALLERY_MAIN_IMAGE);
    }

    private void setMainPhotoFromCam() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempCamFileName = MyApp.getNewFileName();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempCamFileName);
            startActivityForResult(cameraIntent, RESULT_CAM_MAIN_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CAM_MAIN_IMAGE) {
            if (tempCamFileName != null) {
                if (resultCode == Activity.RESULT_OK) {
                    MyApp.setPic(tempCamFileName.getPath(), image_main);
                } else {
                    File file = new File(tempCamFileName.getPath());
                    boolean deleted = file.delete();
                }
                tempCamFileName = null;
            }
        } else if (requestCode == RESULT_GALLERY_MAIN_IMAGE && resultCode == RESULT_OK && null != data) {
            String fileName = getFromGallery(data);
            MyApp.setPic(fileName, image_main);
            text_image_main_file.setText(fileName);
        }
    }

    private String getFromGallery(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        File src = new File(cursor.getString(columnIndex));
        File dataFolder = MyApp.getDataFolder();
        dataFolder.mkdirs();
        File dst = new File(dataFolder, src.getName());
        try {
            MyApp.copy(src, dst);
            return dst.getAbsolutePath();
//            MyApp.setPic(dst.getAbsolutePath(), image_main);
//            text_image_main_file.setText(dst.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        cursor.close();
        return null;
    }

    private void showPopupMenu(final View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.cam_gallery_popupmenu); // For Android 4.0
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (view.getId() == R.id.image_main) {
                    switch (item.getItemId()) {
                        case R.id.menu_cam:
                            setMainPhotoFromCam();
                            return true;
                        case R.id.menu_gallery:
                            setMainPhotoFromGallery();
                            return true;
                        default:
                            return false;
                    }
                }
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "onDismiss", Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }
}
