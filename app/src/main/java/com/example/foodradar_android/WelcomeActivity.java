package com.example.foodradar_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.foodradar_android.main.MainActivity;

public class WelcomeActivity extends AppCompatActivity implements Animation.AnimationListener {
    private ImageView imageView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = this;
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageView = (ImageView)findViewById(R.id.imWelcome);
        //imageView.setAlpha(1);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setAnimationListener(this);
        imageView.setAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent().setClass(WelcomeActivity.this, MainActivity.class));
            }
        }, 5000);

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        finish();
//        Intent intent = new Intent();
//        intent.setClass(WelcomeActivity.this, MainActivity.class);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}