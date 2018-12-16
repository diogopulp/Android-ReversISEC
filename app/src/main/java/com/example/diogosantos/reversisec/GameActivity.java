package com.example.diogosantos.reversisec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.example.diogosantos.reversisec.logic.Game;

public class GameActivity extends AppCompatActivity {

    //View
    private GridView gridView;

    //Game
    private String gameType;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridView.setAdapter(game);
    }
}
