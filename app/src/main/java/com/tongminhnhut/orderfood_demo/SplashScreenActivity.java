package com.tongminhnhut.orderfood_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        img = findViewById(R.id.imgIcon_Order);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_icon);
        img.setAnimation(animation);
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Thread timer = new Thread(){
            public void run (){
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
