package com.dicoding.rockman_barbershop.Activity.SplashScreen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;

import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    private ImageView iv_splash;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mediaPlayer=MediaPlayer.create(splash.this, R.raw.selamat_datang);

        iv_splash = findViewById(R.id.iv_splash);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.transition);

        iv_splash.startAnimation(myanim);
        final Intent i = new Intent(this, MainActivity.class);
        Thread timer = new Thread(){
            public void run () {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    mediaPlayer.start();
                    finish();
                }
            }
        };
                timer.start();
    }
}
