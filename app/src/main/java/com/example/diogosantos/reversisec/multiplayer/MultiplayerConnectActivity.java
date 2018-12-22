package com.example.diogosantos.reversisec.multiplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.diogosantos.reversisec.GameActivity;
import com.example.diogosantos.reversisec.R;

public class MultiplayerConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_connect);
        registerButtons();
    }

    public void registerButtons(){
        register(R.id.createGameBT);
        register(R.id.joinGameBT);
    }

    private void register(int buttonResourceId){
        findViewById(buttonResourceId).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v){


            switch (v.getId()) {
                case R.id.createGameBT:
                    onServer(v);
                    break;
                case R.id.joinGameBT:
                    onClient(v);
                    break;
                case View.NO_ID:
                default:
                    break;
            }
        }
    };

    public void onServer(View v)
    {
        Intent intent=new Intent(this, GameActivity.class);
        intent.putExtra("GAME_MODE", GameActivity.SERVER);
        intent.putExtra("GAME_TYPE", false);

        startActivity(intent);
    }

    public void onClient(View v)
    {
        Intent intent=new Intent(this, GameActivity.class);
        intent.putExtra("GAME_MODE", GameActivity.CLIENT);
        intent.putExtra("GAME_TYPE", false);
        startActivity(intent);
    }

}
