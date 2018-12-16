package com.example.diogosantos.reversisec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

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

        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                int currentXPos = (int) event.getX();
                int currentYPos = (int) event.getY();

                int position = gridView.pointToPosition(currentXPos, currentYPos);

                Toast.makeText(GameActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }
}
