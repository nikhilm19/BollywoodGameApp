package com.example.nikmul19.bollywoodgame;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager =getSupportFragmentManager();
        PlayFragment fragment= new PlayFragment();
        fragmentManager.beginTransaction().add(R.id.fragment_container,fragment,null).commit();
    }


}
