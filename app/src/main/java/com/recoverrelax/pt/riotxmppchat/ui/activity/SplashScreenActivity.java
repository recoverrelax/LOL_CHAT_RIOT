package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.MainApplication;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashScreenActivity extends AppCompatActivity {
    // Splash screen timer
    static int SPLASH_TIME_OUT = 3000;
    private static String TIMER_KEY = "splashScheduled";
    private boolean isSplashScheduled;

    @InjectView(R.id.lol_logo)
    ImageView logo;

    private Handler splashHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.isTablet))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_splashscreen);

//        if (savedInstanceState != null  && savedInstanceState.containsKey(TIMER_KEY)) {
//            isSplashScheduled = savedInstanceState.getBoolean(TIMER_KEY);
//        }
//
//        if (!isSplashScheduled) {
//            isSplashScheduled = true;
//            /*New Handler to start the Main activity and
//            close the splash screen after few seconds */
//            //splashHandler.postDelayed(splashRunnable, SPLASH_TIME_OUT);
//
//            new AsyncTask<Void, Void, Void>() {
//
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Void aVoid) {
//                    splashHandler.postDelayed(splashRunnable, SPLASH_TIME_OUT);
//                }
//            }.execute();
//
//        }

        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.1f, 1f, 0.1f);
        logo.setAnimation(scaleAnimation);
        scaleAnimation.start();

        MainApplication.getInstance().setConnectedXmppUser(null);




        showAfterSplashIntent();
    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if the user presses back remove the runnable
        in order to avoid opening the intent*/
        splashHandler.removeCallbacks(splashRunnable);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TIMER_KEY, isSplashScheduled);
        super.onSaveInstanceState(outState);
    }

    /*
    * Showing splash screen with a timer. This will be useful when
    * you want to show case your app logo / company
    */
    private Runnable splashRunnable = new Runnable() {
        // This method will be executed once the timer is over
        // Start your app main activity
        public void run() {
            showAfterSplashIntent();
        }
    };

    private void showAfterSplashIntent() {
        finish();
        Intent it = new Intent(SplashScreenActivity.this,LoginActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(it);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
