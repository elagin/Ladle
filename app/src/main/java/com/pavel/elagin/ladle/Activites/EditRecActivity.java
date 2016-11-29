package com.pavel.elagin.ladle.Activites;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pavel.elagin.ladle.R;

public class EditRecActivity extends AppCompatActivity implements View.OnClickListener {

	Button button_add_ing;
	TableLayout table;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_rec);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		table = (TableLayout) findViewById(R.id.table_rec_ing);
		table.requestLayout();     // Not sure if this is needed.

		button_add_ing = (Button) findViewById(R.id.button_add_ing);
		button_add_ing.setOnClickListener(this);
//        button_add_ing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
		addIng("Соль", 1);
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
				for (int i = 0, j = table.getChildCount(); i < j; i++) {
					View view = table.getChildAt(i);
					if (view instanceof TableRow) {
						TableRow row = (TableRow) view;
						TextView name = (TextView) row.findViewById(R.id.ing_name);
						TextView count = (TextView) row.findViewById(R.id.ing_count);
						String res = name.getText() + " " + count.getText();
						res.length();
					}
				}
				finish();
//				Intent intentAbout = new Intent(getActivity(), AboutActivity.class);
//				this.startActivity(intentAbout);
				return true;
		}
		return false;
	}

	private void addIng(String name, Integer count) {
		final int index = table.getChildCount();

		TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.rec_ing_row, null);
		if(name != null)
			((TextView) row.findViewById(R.id.ing_name)).setText(name);
		if(count != null)
			((TextView) row.findViewById(R.id.ing_count)).setText(count.toString());

		row.setId(index);
		table.addView(row);

		ImageButton btn = (ImageButton) row.findViewById(R.id.dell_ing);
		btn.setId(index);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0, j = table.getChildCount(); i < j; i++) {
					View row = table.getChildAt(i);
					if(row.getId() == v.getId()) {
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
				addIng(null, null);
				break;
		}
	}
}
