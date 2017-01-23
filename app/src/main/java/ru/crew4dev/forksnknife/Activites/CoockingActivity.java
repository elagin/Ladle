package ru.crew4dev.forksnknife.Activites;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ru.crew4dev.forksnknife.AnimateViews;
import ru.crew4dev.forksnknife.MyApp;
import ru.crew4dev.forksnknife.R;
import ru.crew4dev.forksnknife.Recipe;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CoockingActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private ImageButton coocking_step_photo;
    private ImageButton float_photo;
    private View leftCreateWizard;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    //private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private boolean inCreate;
    private boolean isVisiblePhoto;

    private Recipe recipe;
    private Integer recipeID;
    private Integer stepNumber;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coocking);
        inCreate = true;
        mVisible = true;
        isVisiblePhoto = false;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        coocking_step_photo = (ImageButton) findViewById(R.id.coocking_toggle_photo);
        coocking_step_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisiblePhoto) {
                    AnimateViews.hide(leftCreateWizard, AnimateViews.LEFT);
                    isVisiblePhoto = false;
                } else {
                    AnimateViews.show(leftCreateWizard, AnimateViews.LEFT);
                    isVisiblePhoto = true;
                }
            }
        });

        coocking_step_photo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    coocking_step_photo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    coocking_step_photo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                update();
            }
        });

        leftCreateWizard = findViewById(R.id.alignment_type);
        float_photo = (ImageButton) findViewById(R.id.float_photo);
        float_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisiblePhoto) {
                    AnimateViews.hide(leftCreateWizard, AnimateViews.LEFT);
                    isVisiblePhoto = false;
                } else {
                    AnimateViews.show(leftCreateWizard, AnimateViews.LEFT);
                    isVisiblePhoto = true;
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stepNumber < recipe.getStepList().size() - 1) {
                    stepNumber = stepNumber + 1;
                    if (isVisiblePhoto) {
                        AnimateViews.hide(leftCreateWizard, AnimateViews.LEFT);
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
                        AnimateViews.hide(leftCreateWizard, AnimateViews.LEFT);
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
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (inCreate) {
            ViewTreeObserver vto = leftCreateWizard.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    leftCreateWizard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    leftCreateWizard.setTranslationX(-leftCreateWizard.getWidth());
//                    rightCreateWizard.setTranslationX(rightCreateWizard.getWidth());
//                    bottomCreate.setTranslationY(bottomCreate.getHeight());
//                    textNotify.setTranslationY(-textNotify.getHeight());
//                    AnimateViews.hide(targetView);
//                    AnimateViews.show(leftMain);
//                    myApp.getMap().goToUser();
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
                    //MyApp.setPic(step.fileName, (ImageView) findViewById(R.id.coocking_step_photo));
                    MyApp.setPic2(step.fileName, (ImageView) findViewById(R.id.float_photo), MyApp.MATCH_PARENT);
                    //(findViewById(R.id.coocking_step_photo)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.coocking_toggle_photo)).setVisibility(View.VISIBLE);
                } else {
                    //(findViewById(R.id.coocking_step_photo)).setVisibility(View.GONE);
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
