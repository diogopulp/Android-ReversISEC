package com.example.diogosantos.reversisec.multiplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.diogosantos.reversisec.GameActivity;
import com.example.diogosantos.reversisec.R;

public class MultiplayerOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_options);
        registerButtons();
    }

    public void registerButtons(){
        register(R.id.twoPlayer1DevBT);
        register(R.id.twoPlayer2DevBT);
    }

    private void register(int buttonResourceId){
        findViewById(buttonResourceId).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.twoPlayer1DevBT:
                    Intent intentA = new Intent(MultiplayerOptionsActivity.this,GameActivity.class);
                    intentA.putExtra("GAME_MULTI_ONE_DEVICE", true);
                    startActivity(intentA);
                    break;
                case R.id.twoPlayer2DevBT:
                    Intent intentB = new Intent(MultiplayerOptionsActivity.this,MultiplayerConnectActivity.class);
                    startActivity(intentB);
                    break;
                case View.NO_ID:
                default:
                    break;
            }
        }
    };
}
