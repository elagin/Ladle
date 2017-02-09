package ru.crew4dev.forksnknife.Activites;

/**
 * Created by elagin on 09.02.17.
 */

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.crew4dev.forksnknife.AnimateViews;
import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.R;
import ru.crew4dev.forksnknife.Recipe;

public class Fragment_control extends Fragment {

    private ImageButton coocking_toggle_photo;
    private ImageButton float_photo;
    private View float_panel;
    private TextView edit_step_time;
    private TextView coocking_step_number;
    private TextView coocking_step_descr;

    private boolean inCreate;
    private boolean isVisiblePhoto;

    private Recipe recipe;
    private Integer recipeID;
    private Integer stepNumber;


    String tag = this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(tag, "onCreate");
        super.onCreate(savedInstanceState);
        /** Getting the arguments to the Bundle object */
        Bundle data = getArguments();
        if(data != null ) {
            stepNumber = data.getInt("current_step");
            recipeID = data.getInt("current_recipe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(tag, "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_controle, container, false);

        inCreate = true;
        isVisiblePhoto = false;

        edit_step_time = (TextView) view.findViewById(R.id.coocking_step_time);
        coocking_step_number = (TextView) view.findViewById(R.id.coocking_step_number);
        coocking_step_descr = (TextView) view.findViewById(R.id.coocking_step_descr);

        View.OnClickListener photoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisiblePhoto) {
                    AnimateViews.hide(float_panel, AnimateViews.LEFT);
                    isVisiblePhoto = false;
                    toggleDescription(true);
                } else {
                    AnimateViews.show(float_panel, AnimateViews.LEFT);
                    isVisiblePhoto = true;
                    toggleDescription(false);
                }
            }
        };

        coocking_toggle_photo = (ImageButton) view.findViewById(R.id.coocking_toggle_photo);
        coocking_toggle_photo.setOnClickListener(photoClickListener);

        float_photo = (ImageButton) view.findViewById(R.id.float_photo);
        float_photo.setOnClickListener(photoClickListener);
        float_photo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    float_photo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    float_photo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                update(view);
            }
        });

        float_panel = view.findViewById(R.id.float_panel);
        float_panel.setOnClickListener(photoClickListener);

        //((TextView)view.findViewById(R.id.textView)).setText(String.valueOf(stepNum));
        toggleDescription(true);
        update(view);
        return view;
    }

    private void update(View view) {
        if (recipeID != null) {
            recipe = MyApp.getRecipe(recipeID);
            List<Recipe.Step> stepList = recipe.getStepList();
            if (stepList != null && stepList.size() > 0) {
                Recipe.Step step = stepList.get(stepNumber);
                if (step.time != null && step.time > 0) {
                    edit_step_time.setText(String.format(getString(R.string.time_format_w_mins), step.time.toString()));
                    edit_step_time.setVisibility(View.VISIBLE);
                } else
                    edit_step_time.setVisibility(View.GONE);

                ((TextView) view.findViewById(R.id.coocking_step_descr)).setText(step.desc);

                Integer id = stepNumber + 1;
                Integer totalSteps = stepList.size();
                ((TextView) view.findViewById(R.id.coocking_step_number)).setText(String.format(getString(R.string.coocking_step_info), id.toString(), totalSteps.toString()));
                if (step.fileName != null && step.fileName.length() > 0) {
                    MyApp.setPic2(step.fileName, (ImageView) view.findViewById(R.id.float_photo), MyApp.MATCH_PARENT);
                    (view.findViewById(R.id.coocking_toggle_photo)).setVisibility(View.VISIBLE);
                } else {
                    (view.findViewById(R.id.coocking_toggle_photo)).setVisibility(View.GONE);
                }
            } else {
                view.findViewById(R.id.coocking_toggle_photo).setVisibility(View.GONE);
            }
        }
    }

    private void toggleDescription(boolean isShow) {
        if (isShow) {
            coocking_step_number.setVisibility(View.VISIBLE);
            coocking_step_descr.setVisibility(View.VISIBLE);
            edit_step_time.setVisibility(View.VISIBLE);
            coocking_toggle_photo.setVisibility(View.VISIBLE);
            AnimateViews.hide(float_panel, AnimateViews.LEFT);
        } else {
            coocking_step_number.setVisibility(View.GONE);
            coocking_step_descr.setVisibility(View.GONE);
            edit_step_time.setVisibility(View.GONE);
            coocking_toggle_photo.setVisibility(View.GONE);
            AnimateViews.show(float_panel, AnimateViews.LEFT);
        }
    }
}