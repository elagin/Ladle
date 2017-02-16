package ru.crew4dev.forksnknife.Activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.Purchase;
import ru.crew4dev.forksnknife.R;

public class ShoppingActivity extends AppCompatActivity {

    private de.codecrafters.tableview.TableView table_shopping_list_view;
    private List<Purchase> shoppingList;

    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        table_shopping_list_view = (de.codecrafters.tableview.TableView) findViewById(R.id.table_shopping_list_view);
        shoppingList = MyApp.getShopingList();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (shoppingList != null) {
            Collections.sort(shoppingList, new CustomComparator());
            findViewById(R.id.textView_shopping_list_is_empty).setVisibility(View.GONE);
            table_shopping_list_view.removeAllViews();
            for (int i = 0; i < shoppingList.size(); i++) {
                addIng(shoppingList.get(i));
            }
        } else
            findViewById(R.id.textView_shopping_list_is_empty).setVisibility(View.VISIBLE);
    }

    public class CustomComparator implements Comparator<Purchase> {
        @Override
        public int compare(Purchase object1, Purchase object2) {
            //from Boolean.compare:
            return (object1.isBought() == object2.isBought()) ? 0 : (object1.isBought() ? 1 : -1);
//            return object1.getName().compareTo(object2.getName());
        }
    }

    private void setStrikeoutText(HtmlTextView view, boolean isStrike) {
        String original = view.getText().toString();
        if (original != null && original.length() > 0) {
            if (isStrike) {
                StringBuilder html = new StringBuilder();
                html.append("<s>").append(original).append("</s>");
                view.setHtml(html.toString());
            } else
                view.setHtml(original);
        }
    }

    private void setBought(TableRow row, Purchase purchase) {
        setStrikeoutText((HtmlTextView) row.findViewById(R.id.ing_name), purchase.isBought());
        setStrikeoutText((HtmlTextView) row.findViewById(R.id.ing_count), purchase.isBought());
        setStrikeoutText((HtmlTextView) row.findViewById(R.id.ing_unit), purchase.isBought());
    }

    private void addIng(Purchase value) {
        final int index = table_shopping_list_view.getChildCount();
        final TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.shopping_item_row, null);
        final Purchase purchase = value;
        ((HtmlTextView) row.findViewById(R.id.ing_name)).setText(purchase.getName());
        ((HtmlTextView) row.findViewById(R.id.ing_count)).setText(purchase.getCount());
        ((HtmlTextView) row.findViewById(R.id.ing_unit)).setText(purchase.getUnit());
        row.setId(index);
        setBought(row, purchase);
        View shopping_item_panel = row.findViewById(R.id.shopping_item_panel);
        shopping_item_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchase.toggle();
                MyApp.saveShoppingList();
                setBought(row, purchase);
                update();
            }
        });
        table_shopping_list_view.addView(row);
    }
}
