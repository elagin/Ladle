package ru.crew4dev.forksnknife.Activites;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ru.crew4dev.forksnknife.AnimateViews;
import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.R;
import ru.crew4dev.forksnknife.Recipe;

public class CoockingActivity extends AppCompatActivity {
    private ImageButton coocking_toggle_photo;
    private ImageButton float_photo;
    private View float_panel;

    private boolean inCreate;
    private boolean isVisiblePhoto;

    private Recipe recipe;
    private Integer recipeID;
    private Integer stepNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coocking);
        inCreate = true;
        isVisiblePhoto = false;
        float_panel = findViewById(R.id.float_panel);

        View.OnClickListener photoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisiblePhoto) {
                    AnimateViews.hide(float_panel, AnimateViews.LEFT);
                    isVisiblePhoto = false;
                } else {
                    AnimateViews.show(float_panel, AnimateViews.LEFT);
                    isVisiblePhoto = true;
                }
            }
        };

        coocking_toggle_photo = (ImageButton) findViewById(R.id.coocking_toggle_photo);
        coocking_toggle_photo.setOnClickListener(photoClickListener);
//        coocking_toggle_photo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                // Removing layout listener to avoid multiple calls
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                    coocking_toggle_photo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                } else {
//                    coocking_toggle_photo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//                update();
//            }
//        });

        float_photo = (ImageButton) findViewById(R.id.float_photo);
        float_photo.setOnClickListener(photoClickListener);

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stepNumber < recipe.getStepList().size() - 1) {
                    stepNumber = stepNumber + 1;
                    if (isVisiblePhoto) {
                        AnimateViews.hide(float_panel, AnimateViews.LEFT);
                        isVisiblePhoto = false;
                    }
                    update();
                }
            }
        });

        findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stepNumber > 0) {
                    stepNumber = stepNumber - 1;
                    if (isVisiblePhoto) {
                        AnimateViews.hide(float_panel, AnimateViews.LEFT);
                        isVisiblePhoto = false;
                    }
                    update();
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeID = bundle.getInt("recipeID");
            stepNumber = 0;
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && recipeID != null) {
                recipe = MyApp.getRecipe(recipeID);
                actionBar.setTitle(recipe.getName());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (inCreate) {
            ViewTreeObserver vto = float_panel.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    float_panel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    float_panel.setTranslationX(-float_panel.getWidth());
                    inCreate = false;
                }
            });
        }
        update();
    }

    private void update() {
        if (recipeID != null) {
            recipe = MyApp.getRecipe(recipeID);
            if (recipe.getStepList().size() > 0) {
                Recipe.Step step = recipe.getStepList().get(stepNumber);

                TextView edit_step_time = (TextView) findViewById(R.id.coocking_step_time);
                if (step.time != null && step.time > 0) {
                    edit_step_time.setText(String.format(getString(R.string.time_format_w_mins), step.time.toString()));
                    edit_step_time.setVisibility(View.VISIBLE);
                } else
                    edit_step_time.setVisibility(View.GONE);

                ((TextView) findViewById(R.id.coocking_step_descr)).setText(step.desc);

                Integer id = stepNumber + 1;
                Integer totalSteps = recipe.getStepList().size();
                ((TextView) findViewById(R.id.coocking_step_number)).setText(String.format(getString(R.string.coocking_step_info), id.toString(), totalSteps.toString()));
                if (step.fileName != null && step.fileName.length() > 0) {
                    MyApp.setPic2(step.fileName, (ImageView) findViewById(R.id.float_photo), MyApp.MATCH_PARENT);
                    (findViewById(R.id.coocking_toggle_photo)).setVisibility(View.VISIBLE);
                } else {
                    (findViewById(R.id.coocking_toggle_photo)).setVisibility(View.GONE);
                }
                findViewById(R.id.next_button).setEnabled(stepNumber < recipe.getStepList().size() - 1);
                findViewById(R.id.prev_button).setEnabled(stepNumber != 0);
            } else {
                findViewById(R.id.coocking_toggle_photo).setVisibility(View.GONE);
                findViewById(R.id.next_button).setVisibility(View.GONE);
                findViewById(R.id.prev_button).setVisibility(View.GONE);
            }
        }
    }
}
