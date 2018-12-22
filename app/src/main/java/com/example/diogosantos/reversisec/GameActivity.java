package com.example.diogosantos.reversisec;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.example.diogosantos.reversisec.logic.Game;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameActivity extends AppCompatActivity {

    public static final int SERVER = 0;
    public static final int CLIENT = 1;
    public static final int ME = 0;
    public static final int OTHER = 1;


    int mode = -1;

    private static final int PORT = 8899;
    private static final int PORTaux = 9988; // to test with emulators

    ProgressDialog pd = null;

    ServerSocket serverSocket = null;
    Socket socketGame = null;
    BufferedReader input;
    PrintWriter output;
    Handler procMsg = null;

    boolean isSinglePlayer;


    int height, width;

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

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    game.placePiece(position);
                    Toast.makeText(GameActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                    game.notifyDataSetChanged();
                }

                return false;
            }
        });
    }
}
