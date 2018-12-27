package com.example.diogosantos.reversisec.singleplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.diogosantos.reversisec.GameActivity;
import com.example.diogosantos.reversisec.R;

public class SinglePlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);
        registerButtons();
    }

    public void registerButtons(){
        register(R.id.newGameBT);
        register(R.id.loadGameBT);
    }

    private void register(int buttonResourceId){
        findViewById(buttonResourceId).setOnClickListener(buttonClickListener);
    }


    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.newGameBT:
                    Intent intent = new Intent(SinglePlayerActivity.this,GameActivity.class);
                    intent.putExtra("GAME_SINGLE", true);
                    startActivity(intent);
                    break;
                case R.id.loadGameBT:
                    break;
                case View.NO_ID:
                default:
                    // TODO Auto-generated method stub
                    break;
            }
        }
    };
}
