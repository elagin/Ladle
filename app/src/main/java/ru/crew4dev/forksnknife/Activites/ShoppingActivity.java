package ru.crew4dev.forksnknife.Activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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

        List<Ingredient> shoppingList = MyApp.getShopingList();
        if (shoppingList != null)
            for (int i = 0; i < shoppingList.size(); i++) {
                Ingredient item = shoppingList.get(i);
                addIng(item.name, item.count, item.unit);
            }
    }

    private void addIng(String name, Double count, String unit) {
        final int index = table_shopping_list_view.getChildCount();
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.shopping_item_row, null);
        ((TextView) row.findViewById(R.id.ing_name)).setText(name);
        ((TextView) row.findViewById(R.id.ing_count)).setText(count.toString().replace(".0", ""));
        ((TextView) row.findViewById(R.id.ing_unit)).setText(unit);
        row.setId(index);
        table_shopping_list_view.addView(row);
    }
}
