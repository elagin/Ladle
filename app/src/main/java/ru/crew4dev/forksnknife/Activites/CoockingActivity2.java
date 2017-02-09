package ru.crew4dev.forksnknife.Activites;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import ru.crew4dev.forksnknife.R;

public class CoockingActivity2 extends FragmentActivity {

    //private Recipe recipe;
    private Integer recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coocking2);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeID = bundle.getInt("recipeID");
            //recipe = MyApp.getRecipe(recipeID);

            /** Instantiating FragmentPagerAdapter */
            Fragment_Pager pagerAdapter = new Fragment_Pager(fm, recipeID);

            /** Setting the pagerAdapter to the pager object */
            pager.setAdapter(pagerAdapter);
        }
    }
}
