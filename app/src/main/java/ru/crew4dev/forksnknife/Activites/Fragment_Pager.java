package ru.crew4dev.forksnknife.Activites;

/**
 * Created by elagin on 09.02.17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.Recipe;

public class Fragment_Pager extends FragmentPagerAdapter {

    private Integer recipeID;
    int pageCount = 0;

    public Fragment_Pager(FragmentManager fm, Integer recipeId) {
        super(fm);
        // TODO Auto-generated constructor stub

        this.recipeID = recipeId;
        Recipe recipe = MyApp.getRecipe(recipeID);
        if (recipe != null) {
            pageCount = recipe.getStepList().size();
        }
    }

    @Override
    public Fragment getItem(int arg0) {
        Fragment_control myFragment = new Fragment_control();
        Bundle data = new Bundle();
        data.putInt("current_recipe", recipeID);
        data.putInt("current_step", arg0);
        myFragment.setArguments(data);
        return myFragment;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return pageCount;
    }
}
