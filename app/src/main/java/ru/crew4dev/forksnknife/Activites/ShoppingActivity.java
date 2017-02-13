package ru.crew4dev.forksnknife.Activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import ru.crew4dev.forksnknife.Ingredient;
import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.R;

public class ShoppingActivity extends AppCompatActivity {

    TableLayout table_shopping_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        table_shopping_list_view = (TableLayout) findViewById(R.id.table_shopping_list_view);
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shopping_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_shopping_cart:
                MyApp.clearShopingList();
                update();
                return true;
        }
        return false;
    }

    private void update() {
        List<Ingredient> shoppingList = MyApp.getShopingList();
        if (shoppingList != null) {
            findViewById(R.id.textView_shopping_list_is_empty).setVisibility(View.GONE);
            table_shopping_list_view.removeAllViews();
            for (int i = 0; i < shoppingList.size(); i++) {
                Ingredient Ingredient = shoppingList.get(i);
                addIng(Ingredient);
            }
        } else
            findViewById(R.id.textView_shopping_list_is_empty).setVisibility(View.VISIBLE);
    }

    private void addIng(Ingredient ingredient) {
        final int index = table_shopping_list_view.getChildCount();
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.shopping_item_row, null);
        ((TextView) row.findViewById(R.id.ing_name)).setText(ingredient.name);
        ((TextView) row.findViewById(R.id.ing_count)).setText(ingredient.count.toString().replace(".0", ""));
        ((TextView) row.findViewById(R.id.ing_unit)).setText(ingredient.unit);
        row.setId(index);
        table_shopping_list_view.addView(row);
    }
}
