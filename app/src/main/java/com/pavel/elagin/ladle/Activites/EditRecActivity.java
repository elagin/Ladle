package com.pavel.elagin.ladle.Activites;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pavel.elagin.ladle.MyApp;
import com.pavel.elagin.ladle.R;
import com.pavel.elagin.ladle.Recipe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class EditRecActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "myLogs";

    private TableLayout table;
    private TextView edit_rec_name;
    private TextView edit_rec_descr;
    private TextView edit_rec_total_time_count;
    private TextView edit_rec_steps;
    private Integer recipeID;

    static final int RESULT_LOAD_IMAGE = 452;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    int TAKE_PHOTO_CODE = 745;
    public static int count = 0;

//    private String dir;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rec);

        imageView = (ImageView) findViewById(R.id.imageView2);

        table = (TableLayout) findViewById(R.id.table_rec_ing);
        table.requestLayout();     // Not sure if this is needed.

        ImageButton button_add_ing = (ImageButton) findViewById(R.id.button_add_ing);
        button_add_ing.setOnClickListener(this);

        edit_rec_name = (TextView) findViewById(R.id.edit_rec_name);
        edit_rec_descr = (TextView) findViewById(R.id.edit_rec_descr);
        edit_rec_steps = (TextView) findViewById(R.id.edit_rec_steps);
        edit_rec_total_time_count = (TextView) findViewById(R.id.edit_rec_total_time_count);

        Button button_get_photo = (Button) findViewById(R.id.button_get_photo);
        button_get_photo.setOnClickListener(this);

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
            edit_rec_name.setText(recipe.getName());
            edit_rec_descr.setText(recipe.getDescription());
            edit_rec_steps.setText(recipe.getSteps());
            edit_rec_total_time_count.setText(recipe.getTotalTime().toString());
            List<Recipe.Ingredient> ingredientList = recipe.getIngredients();
            for (int i = 0; i < ingredientList.size(); i++) {
                Recipe.Ingredient item = ingredientList.get(i);
                addIng(item.name, item.count, item.unit);
            }
        } else
            addIng(null, null, null);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
                MyApp.saveRecipesJSon(this, true);
                finish();
                return true;
            case R.id.action_cancel_rec:
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
            case R.id.button_get_photo:
                addPhoto();
                break;
            case R.id.button_get_photo_cam:
                //dispatchTakePictureIntent();

                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);

                // Here, the counter will be incremented each time, and the
                // picture taken by camera will be stored as 1.jpg,2.jpg
                // and likewise.
                count++;
                String file = MyApp.getDataFolder().getAbsolutePath() + "/" + count + ".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                } catch (IOException e) {
                    Log.d(TAG, "Save photo " + e.getLocalizedMessage());
                }

                Uri outputFileUri = Uri.fromFile(newfile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                break;
        }
    }

    private void addPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

//    static final int REQUEST_TAKE_PHOTO = 3;
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                //...
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this, "com.pavel.elagin.ladle.fileprovider", photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }

    private static final int CAMERA_REQUEST = 1888;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        } else if (requestCode == TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            // data is null;
            if (data != null) {

            }
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            File src = new File(picturePath);

            File dataFolder = MyApp.getDataFolder();
            dataFolder.mkdirs();
            File dst = new File(dataFolder, src.getName());
            try {
                copy(src, dst);
                setPic(dst.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
            cursor.close();
        }
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            imageView.setImageBitmap(imageBitmap);
        } else if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }
*/
    //String mCurrentPhotoPath;

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

    private void setPic(String mCurrentPhotoPath) {
        ImageView mImageView = (ImageView) findViewById(R.id.imageView2);
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
}
