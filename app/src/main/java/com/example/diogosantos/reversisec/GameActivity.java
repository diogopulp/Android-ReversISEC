package com.example.diogosantos.reversisec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.GridView;

import com.example.diogosantos.reversisec.logic.Game;

public class GameActivity extends AppCompatActivity {

    //View
    private GridView gridView;

    //Game
    private String gameType;
    private Game game;

    //Device
    private int deviceHeight;
    private int deviceWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DisplayMetrics displayMetrics;
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;

        game = new Game(this, deviceHeight, deviceWidth);

        gridView = findViewById(R.id.playBoard);
        gridView.setAdapter(game);
    }
}
