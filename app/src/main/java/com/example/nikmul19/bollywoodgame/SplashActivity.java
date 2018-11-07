package com.example.nikmul19.bollywoodgame;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ProgressBar progressBar;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View view=getWindow().getDecorView();
        int uiOpts=View.SYSTEM_UI_FLAG_FULLSCREEN;
        view.setSystemUiVisibility(uiOpts);

        progressBar=findViewById(R.id.progressBar2);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                progressBar.setIndeterminate(false);
                SplashActivity.this.finish();



            }
        },5000);
    }
}
