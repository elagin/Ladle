package ru.crew4dev.forksnknife.Activites;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.crew4dev.forksnknife.ConfirmDialogStepDeleteFragment;
import ru.crew4dev.forksnknife.Ingredient;
import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.Preferences;
import ru.crew4dev.forksnknife.R;
import ru.crew4dev.forksnknife.Recipe;

public class EditRecActivity extends AppCompatActivity implements View.OnClickListener, ConfirmDialogStepDeleteFragment.ConfirmDialogStepDeleteListener {

    private static final String TAG = "myLogs";
    private static final int RESULT_GALLERY_MAIN_IMAGE = 1;
    private static final int RESULT_CAM_MAIN_IMAGE = 2;
    private static final int RESULT_CAM_STEP_IMAGE = 3;
    private static final int RESULT_GALLERY_STEP_IMAGE = 4;

    private TableLayout table;
    private TableLayout edit_rec_steps_table;
    private TextView edit_rec_name;
    private TextView edit_rec_descr;
    private TextView edit_rec_tags;
    private TextView edit_rec_total_time_count;
    private TextView edit_rec_steps;
    private ImageButton image_main;
    private ImageButton image_main_delete;
    private Uri tempCamFileName;
    private Integer recipeID;

    private String mainPhotoFileName;
    //private String stepPhotoFileNameList;
    private Integer changePhotoStepID;
    private String stepPhotoFileName;

    private static final String STATE_MAIN_PHOTO = "main_photo";
    private static final String STATE_STEPS_PHOTO = "steps_photo";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_MAIN_PHOTO, mainPhotoFileName);
        savedInstanceState.putString(STATE_STEPS_PHOTO, saveStepPhotoFileNames());

//        Bitmap bitmap = new Bitmap();
//        savedInstanceState.putParcelable("bitmap", bitmap);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

//    public MakingListObject(Parcel in) {
//        List<Bitmap> list = new ArrayList<>();
//        in.readTypedList(list, Bitmap.CREATOR); // read
//    }

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

        image_main_delete = (ImageButton) findViewById(R.id.image_main_delete);
        image_main_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_main.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_camera, null));
                if (mainPhotoFileName != null && mainPhotoFileName.length() > 0) {
                    MyApp.fileDelete(mainPhotoFileName);
                    mainPhotoFileName = "";
                    image_main_delete.setVisibility(View.GONE);
                }
            }
        });

        image_main = (ImageButton) findViewById(R.id.image_main);
        image_main.setOnClickListener(imageMainClickListener);
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

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mainPhotoFileName = savedInstanceState.getString(STATE_MAIN_PHOTO);
//            stepPhotoFileNameList = savedInstanceState.getString(STATE_STEPS_PHOTO);
//            stepPhotoFileNameList.split(",");
        }

        edit_rec_name = (TextView) findViewById(R.id.edit_rec_name);
        edit_rec_descr = (TextView) findViewById(R.id.edit_rec_descr);
        edit_rec_tags = (TextView) findViewById(R.id.edit_rec_tags);
        edit_rec_steps = (TextView) findViewById(R.id.edit_rec_steps);
        edit_rec_total_time_count = (TextView) findViewById(R.id.edit_rec_total_time_count);

        ImageButton button_add_step = (ImageButton) findViewById(R.id.button_add_step);
        button_add_step.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeID = bundle.getInt("recipeID");
            Recipe recipe = MyApp.getRecipe(recipeID);
            if (recipe == null) {
                Toast.makeText(this, getString(R.string.error_recipe_is_not_found), Toast.LENGTH_LONG).show();
                return;
            }

            if (recipe.getPhoto() != null && recipe.getPhoto().length() > 0) {
                mainPhotoFileName = recipe.getPhoto();
            }

            edit_rec_name.setText(recipe.getName());
            edit_rec_descr.setText(recipe.getDescription());
            edit_rec_tags.setText(recipe.getTags());
            if (recipe.getSteps() != null && recipe.getSteps().length() > 0)
                edit_rec_steps.setText(recipe.getSteps());
            else
                edit_rec_steps.setVisibility(View.GONE);
            if (recipe.getTotalTime() != null && recipe.getTotalTime().toString().length() > 0)
                edit_rec_total_time_count.setText(String.format("%d", recipe.getTotalTime()));
            List<Ingredient> ingredientList = recipe.getIngredients();
            for (int i = 0; i < ingredientList.size(); i++) {
                Ingredient item = ingredientList.get(i);
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
    }

    private View.OnClickListener imageMainClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenuImageMain(v);
        }
    };

    View.OnClickListener imageStepClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenuImageStep(v);
        }
    };

    private void update() {
        if (mainPhotoFileName != null
                && mainPhotoFileName.length() > 0
                && MyApp.setPic(mainPhotoFileName, image_main)) {
            image_main_delete.setVisibility(View.VISIBLE);
        } else {
            image_main_delete.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_rec_menu, menu);
        return true;
    }

    private String saveStepPhotoFileNames() {
        StringBuilder res = new StringBuilder();
        for (int i = 0, j = edit_rec_steps_table.getChildCount(); i < j; i++) {
            View view = edit_rec_steps_table.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                String photoUrl = ((TextView) row.findViewById(R.id.text_photo_url)).getText().toString();
                if (res.length() > 0)
                    res.append(",");
//                if(photoUrl.length() > 0)
                res.append(photoUrl);
//                else
//                    res.append(",");
            }
        }
        return res.toString();
    }

    private boolean saveRecipe(String recipeName) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeName);
        recipe.setDescription(edit_rec_descr.getText().toString());
        recipe.setTags(edit_rec_tags.getText().toString());
        recipe.setSteps(edit_rec_steps.getText().toString());
        String totalTime = edit_rec_total_time_count.getText().toString();
        if (!totalTime.isEmpty()) {
            if (totalTime.length() > 5) {
                Toast.makeText(this, getString(R.string.invalid_time), Toast.LENGTH_LONG).show();
                return false;
            }
            recipe.setTotalTime(Integer.valueOf(totalTime));
        }
        for (int i = 0, j = table.getChildCount(); i < j; i++) {
            View view = table.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                String name = ((TextView) row.findViewById(R.id.ing_name)).getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(this, getString(R.string.rec_name_ing_is_empty), Toast.LENGTH_LONG).show();
                    return false;
                }
                String count = ((TextView) row.findViewById(R.id.ing_count)).getText().toString();
                if (count.isEmpty()) {
                    Toast.makeText(this, getString(R.string.rec_volume_ing_is_empty), Toast.LENGTH_LONG).show();
                    return false;
                }
                String unit = ((TextView) row.findViewById(R.id.ing_unit)).getText().toString();
                if (unit.isEmpty()) {
                    Toast.makeText(this, getString(R.string.rec_unit_ing_is_empty), Toast.LENGTH_LONG).show();
                    return false;
                }
                try {
//  Todo Сделать запись String.format("%d") /чтение по локали
//                    try {
//                        Locale locale;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            locale = getResources().getConfiguration().getLocales().get(0);
//                        } else {
//                            locale = getResources().getConfiguration().locale;
//                        }
//                        NumberFormat format = NumberFormat.getInstance(locale);
//                        try {
//                            Number number = format.parse(count);
//                            Double d = number.doubleValue();
//                            recipe.addIngredient(name, d, unit);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                            Toast.makeText(this, String.format(getString(R.string.error_parse_volume), name), Toast.LENGTH_LONG).show();
//                        }
//                        //recipe.addIngredient(name, Double.valueOf(count), unit);
//                    } catch (NumberFormatException ex) {
//                        Toast.makeText(this, String.format(getString(R.string.error_parse_volume), name), Toast.LENGTH_LONG).show();
//                        return false;
//                    }
                    recipe.addIngredient(name, Double.valueOf(count), unit);
                } catch (NumberFormatException ex) {
                    Toast.makeText(this, String.format(getString(R.string.error_parse_volume), name), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        if (edit_rec_steps_table.getChildCount() == 0) {
            Toast.makeText(this, getString(R.string.rec_steps_empty), Toast.LENGTH_LONG).show();
            return false;
        }

        for (int i = 0, j = edit_rec_steps_table.getChildCount(); i < j; i++) {
            View view = edit_rec_steps_table.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                Integer time = null;
                String timeStr = ((TextView) row.findViewById(R.id.coocking_step_time)).getText().toString();
                if (!timeStr.isEmpty()) {
                    if (timeStr.length() > 5) {
                        Toast.makeText(this, getString(R.string.invalid_time), Toast.LENGTH_LONG).show();
                        return false;
                    }
                    time = Integer.valueOf(timeStr);
                }
                String descr = ((TextView) row.findViewById(R.id.coocking_step_descr)).getText().toString();
                if (descr.isEmpty()) {
                    Integer stepNumber = i + 1;
                    Toast.makeText(this, String.format(getString(R.string.rec_step_descr_is_empty), stepNumber.toString()), Toast.LENGTH_LONG).show();
                    return false;
                }
                String photoUrl = ((TextView) row.findViewById(R.id.text_photo_url)).getText().toString();
                recipe.addStep(photoUrl, descr, time);
            }
        }

        if (mainPhotoFileName != null && mainPhotoFileName.length() > 0)
            recipe.setPhoto(mainPhotoFileName);

        if (recipeID == null)
            recipe.setUid(MyApp.newId());
        else
            recipe.setUid(recipeID);

        MyApp.updateRecipe(recipe);
        MyApp.saveRecipesJSon(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_rec:
                String recipeName = edit_rec_name.getText().toString();
                if (recipeName.length() > 0) {
                    if (saveRecipe(recipeName)) {
                        finish();
                        return true;
                    }
                } else {
                    Toast.makeText(this, getString(R.string.rec_name_is_empty), Toast.LENGTH_LONG).show();
                    return false;
                }
                break;
            case R.id.action_cancel_rec:
                finish();
                return true;
        }
        return false;
    }

    private void addStep(Recipe.Step step) {
        final int index = edit_rec_steps_table.getChildCount();
        final TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.prepare_step_row, null);
        ImageButton image_view_step = (ImageButton) row.findViewById(R.id.step_photo_img);
        image_view_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rowId = ((TextView) row.findViewById(R.id.row_id)).getText().toString();
                changePhotoStepID = Integer.valueOf(rowId);
                showPopupMenuImageStep(v);
            }
        });

//        imageStepClickListener

        ImageButton step_photo_delete = (ImageButton) row.findViewById(R.id.step_photo_delete);
        step_photo_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rowId = ((TextView) row.findViewById(R.id.row_id)).getText().toString();
                changePhotoStepID = Integer.valueOf(rowId);
                TextView viewFileName = (TextView) row.findViewById(R.id.text_photo_url);
                if (viewFileName.getText().length() > 0) {
                    MyApp.fileDelete(viewFileName.getText().toString());
                    viewFileName.setText("");
                    view.setVisibility(View.GONE);
                    ((ImageButton) row.findViewById(R.id.step_photo_img)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_camera, null));
                }
            }
        });

        if (step != null) {
            if (step.fileName != null && step.fileName.length() > 0) {
                TextView text_photo_url = (TextView) row.findViewById(R.id.text_photo_url);
                text_photo_url.setText(step.fileName);
                MyApp.setPic(step.fileName, image_view_step);
                step_photo_delete.setVisibility(View.VISIBLE);
            } else {
                step_photo_delete.setVisibility(View.GONE);
            }
            TextView edit_step_descr = (TextView) row.findViewById(R.id.coocking_step_descr);
            edit_step_descr.setText(step.desc);
            if (step.time != null) {
                TextView edit_step_time = (TextView) row.findViewById(R.id.coocking_step_time);
                edit_step_time.setText(String.format("%d", step.time));
            }
        } else {
            step_photo_delete.setVisibility(View.GONE);
        }

        edit_rec_steps_table.addView(row);
        ((TextView) row.findViewById(R.id.row_id)).setText(String.valueOf(index));

        (row.findViewById(R.id.dell_step)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_rec_steps_table.getChildCount() > 1) {
                    String rowId = ((TextView) row.findViewById(R.id.row_id)).getText().toString();
                    if (!rowId.isEmpty()) {
                        Bundle bundle = new Bundle();
                        Integer showRowId = Integer.valueOf(rowId);
                        bundle.putString("message", String.format(getString(R.string.delete_step_confirm), ++showRowId));
                        bundle.putString("id", rowId);
                        DialogFragment dialog = new ConfirmDialogStepDeleteFragment();
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), "ConfirmDialogStepDeleteFragment");
                    }
                } else {
                    Toast.makeText(MyApp.getContext(), getString(R.string.last_step), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDialogImportPositiveClick(DialogFragment dialog, String id) {
        Log.d(TAG, "User touched the dialog's positive button");
        for (int i = 0, j = edit_rec_steps_table.getChildCount(); i < j; i++) {
            View row = edit_rec_steps_table.getChildAt(i);
            String rowId = ((TextView) row.findViewById(R.id.row_id)).getText().toString();
            if (rowId.equals(id)) {
                edit_rec_steps_table.removeViewAt(i);
                if (recipeID != null) {
                    Recipe recipe = MyApp.getRecipe(recipeID);
                    if (recipe != null)
                        recipe.deleteStep(Integer.valueOf(id));
                }
                break;

            }
        }
        //Reindex row_index
        for (int i = 0, j = edit_rec_steps_table.getChildCount(); i < j; i++) {
            View row = edit_rec_steps_table.getChildAt(i);
            ((TextView) row.findViewById(R.id.row_id)).setText(String.valueOf(i));
        }
    }

    @Override
    public void onDialogImportNegativeClick(DialogFragment dialog) {
    }

    private void addIng(String name, Double count, String unit) {
        final int index = table.getChildCount();
        Log.d(TAG, "addIng: " + String.valueOf(index));
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.rec_ing_row, null);
        if (name != null)
            ((TextView) row.findViewById(R.id.ing_name)).setText(name);
        if (count != null)
            ((TextView) row.findViewById(R.id.ing_count)).setText(count.toString());
        //todo Double нужно вставлять аккуратно
        //((TextView) row.findViewById(R.id.ing_count)).setText(String.format("%d", count.toString()));

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

    private void setMainPhotoFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(i, RESULT_GALLERY_MAIN_IMAGE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.gallery_not_avaible), Toast.LENGTH_LONG).show();
        }
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

    private void setStepPhotoFromCam() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempCamFileName = MyApp.getNewFileName();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempCamFileName);
            startActivityForResult(cameraIntent, RESULT_CAM_STEP_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStepPhotoFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(i, RESULT_GALLERY_STEP_IMAGE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.gallery_not_avaible), Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_CAM_MAIN_IMAGE:
                if (tempCamFileName != null) {
                    if (resultCode == RESULT_OK) {
                        changeMainImage(tempCamFileName.getPath());
                    } else {
                        MyApp.fileDelete(tempCamFileName.getPath());
                    }
                    tempCamFileName = null;
                }
                break;
            case RESULT_GALLERY_MAIN_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    String fileName = getFromGallery(data);
                    if (fileName != null)
                        changeMainImage(fileName);
                }
                break;
            case RESULT_CAM_STEP_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (tempCamFileName != null) {
                        if (resultCode == RESULT_OK) {
                            changeStepImage(tempCamFileName.getPath());
                        } else {
                            MyApp.fileDelete(tempCamFileName.getPath());
                        }
                        tempCamFileName = null;
                    }
                }
                break;
            case RESULT_GALLERY_STEP_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    String fileName = getFromGallery(data);
                    changeStepImage(fileName);
                }
                break;
        }
    }

    private void changeMainImage(String fileName) {
        String oldFile = mainPhotoFileName;
        if (oldFile != null && oldFile.length() > 0) {
            MyApp.fileDelete(oldFile);
        }
        if (MyApp.setPic(fileName, image_main)) {
            mainPhotoFileName = fileName;
            image_main_delete.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, getString(R.string.error_load_image), Toast.LENGTH_LONG).show();
        }
    }

    private void changeStepImage(String fileName) {
        String oldFile = stepPhotoFileName;
        if (oldFile != null && oldFile.length() > 0) {
            MyApp.fileDelete(oldFile);
        }
        TableRow row = (TableRow) edit_rec_steps_table.getChildAt(changePhotoStepID);
        ImageButton imageButton = (ImageButton) row.findViewById(R.id.step_photo_img);
        ((TextView) row.findViewById(R.id.text_photo_url)).setText(fileName);

        View step_photo_delete = row.findViewById(R.id.step_photo_delete);
        if (MyApp.setPic(fileName, imageButton)) {
            step_photo_delete.setVisibility(View.VISIBLE);
            stepPhotoFileName = null;
        } else {
            Toast.makeText(this, getString(R.string.error_load_image), Toast.LENGTH_LONG).show();
        }
    }

    private String getFromGallery(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            File src = new File(cursor.getString(cursor.getColumnIndex(filePathColumn[0])));
            String syncFolder;
            try {
                syncFolder = Preferences.getSyncFolder(this);
                File dataFolder = new File(syncFolder);
                File dst = new File(dataFolder, src.getName());
                try {
                    MyApp.fileCopy(src, dst);
                    return dst.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, String.format(getString(R.string.error_copy_file), dataFolder.getAbsoluteFile()), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, String.format(getString(R.string.error_work_folder), e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            cursor.close();
        }
        return null;
    }

    private void showPopupMenuImageMain(final View view) {
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

//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(getApplicationContext(), "onDismiss", Toast.LENGTH_SHORT).show();
//            }
//        });
        popupMenu.show();
    }

    private void showPopupMenuImageStep(final View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.cam_gallery_popupmenu); // For Android 4.0
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (view.getId() == R.id.step_photo_img) {
                    switch (item.getItemId()) {
                        case R.id.menu_cam:
                            setStepPhotoFromCam();
                            return true;
                        case R.id.menu_gallery:
                            setStepPhotoFromGallery();
                            return true;
                        default:
                            return false;
                    }
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
