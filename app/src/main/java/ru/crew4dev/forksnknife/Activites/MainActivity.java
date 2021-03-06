package ru.crew4dev.forksnknife.Activites;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.crew4dev.forksnknife.ConfirmDialogFragment;
import ru.crew4dev.forksnknife.ConfirmDialogImportFragment;
import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.R;
import ru.crew4dev.forksnknife.Recipe;

import static ru.crew4dev.forksnknife.MyApp.getAppContext;

public class MainActivity extends AppCompatActivity implements ConfirmDialogFragment.ConfirmDialogListener, ConfirmDialogImportFragment.ConfirmDialogImportListener {

    private TableLayout table;

    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp.setCurrentActivity(this);

        table = (TableLayout) findViewById(R.id.main_rec_table);
        table.requestLayout();     // Not sure if this is needed.

        MyApp.loadRecipesJSon(true);
        registerForContextMenu(table);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_rec_table, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        Log.d(TAG, "update");
        table.removeAllViews();
        final List<Recipe> recipes = MyApp.getRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe item = recipes.get(i);
            try {
                Log.d(TAG, "Insert " + item.getUid() + " : " + item.getName());
                TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.recipe_row, null);
                ((TextView) row.findViewById(R.id.rec_name_row)).setText(item.getName());
                if (item.getDescription() != null && item.getDescription().isEmpty()) {
                    (row.findViewById(R.id.rec_desc_row)).setVisibility(View.GONE);
                } else {
                    ((TextView) row.findViewById(R.id.rec_desc_row)).setText(item.getDescription());
                }
                String totalTime = item.getTotalStepTimeString();
                if (totalTime.length() > 0) {
                    ((TextView) row.findViewById(R.id.rec_total_time)).setText(totalTime);
                } else {
                    (row.findViewById(R.id.imageViewClock)).setVisibility(View.GONE);
                    (row.findViewById(R.id.rec_total_time)).setVisibility(View.GONE);
                }
                row.setId(item.getUid());
                row.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        MyApp.toDetails(v.getId());
                    }
                });
                row.setLongClickable(true);
                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showNoticeDeleteRecipeDialog(v.getId());
                        return true;
                    }
                });
                table.addView(row);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        menu.setHeaderTitle(".....");
        inflater.inflate(R.menu.main_rec_table, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_rec_delete:
                //stuff
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showNoticeDeleteRecipeDialog(int id) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmDialogFragment();
        Recipe recipe = MyApp.getRecipe(id);
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("message", String.format(getString(R.string.delete_confirm), recipe.getName()));
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "ConfirmDialogFragment");
    }

    private void showNoticeImportRecipeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmDialogImportFragment();
        Bundle bundle = new Bundle();
        bundle.putString("message", getString(R.string.rewrite_recipe));
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "ConfirmDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int id) {
        Log.d(TAG, "User touched the dialog's positive button");
        for (int i = 0; i < table.getChildCount(); i++) {
            View view = table.getChildAt(i);
            if (view.getId() == id) {
                table.removeView(view);
                MyApp.removeRecipe(id);
                MyApp.saveRecipesJSon(true);
                return;
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "User touched the dialog's negative button");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogImportPositiveClick(DialogFragment dialog, int id) {
        Log.d(TAG, "User touched the dialog's positive button");
        loadRecipes();
    }

    @Override
    public void onDialogImportNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "User touched the dialog's negative button");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getAppContext(), EditRecActivity.class);
                Log.d(TAG, "create new recipe");
                startActivity(intent);
                return true;
            case R.id.menu_about:
                MyApp.toAbout();
                return true;
            case R.id.menu_help:
                MyApp.toHelp(this);
                return true;
            case R.id.action_show_shopping_cart:
                MyApp.toShoppingList();
                return true;
            case R.id.menu_settings:
                MyApp.toSettings();
                return true;
            case R.id.menu_export:
                if (MyApp.saveRecipesJSon(false))
                    Toast.makeText(this, getString(R.string.export_succsess), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, getString(R.string.error_export), Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_import:
                if (MyApp.getRecipes().size() > 0) {
                    showNoticeImportRecipeDialog();
                } else {
                    loadRecipes();
                }
                return true;
            case R.id.menu_clearFolder:
                MyApp.deletePhotos();
                return true;
            case R.id.menu_import_rec:
                int newItems = MyApp.loadRecipe();
                if (newItems > 0) {
                    update();
                    Toast.makeText(this, String.format(getString(R.string.import_succsess_count), newItems), Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadRecipes() {
        if (MyApp.loadRecipesJSon(false)) {
            Toast.makeText(this, getString(R.string.import_succsess), Toast.LENGTH_LONG).show();
            MyApp.saveRecipesJSon(true);
            update();
        } else
            Toast.makeText(this, getString(R.string.error_import), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) return;
        switch (requestCode) {
            case MyApp.SDCARD_PERMISSION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (this.checkSelfPermission(Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED)
                        MyApp.permissionRequested = false;
                }
                break;
            default:
                //initSecuredComponents();
        }
    }
}
