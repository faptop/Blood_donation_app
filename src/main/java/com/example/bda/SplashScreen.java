package com.example.bda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity{
        private ImageView logo;
        private TextView title,slogan;
        Animation top_anim,bottom_anim;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to expand splash screen to full window
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        logo=findViewById(R.id.logo);
        title=findViewById(R.id.Title);
        slogan=findViewById(R.id.slogan);

        top_anim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom_anim=AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        logo.setAnimation(top_anim);
        title.setAnimation(bottom_anim);
        slogan.setAnimation(bottom_anim);

//        jumping to next activity
        int SPLASH_SCREEN=4300;
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        Intent intent=new Intent(SplashScreen.this,LoginActivity.class);
                       startActivity(intent);
                       finish(); // we don't want to come back
                }
        },SPLASH_SCREEN);
    }
}