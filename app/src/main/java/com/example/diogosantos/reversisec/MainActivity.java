package com.example.diogosantos.reversisec;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.diogosantos.reversisec.multiplayer.MultiplayerOptionsActivity;
import com.example.diogosantos.reversisec.singleplayer.SinglePlayerActivity;

public class MainActivity extends AppCompatActivity {

    Button btn_take_photo;
    private DrawerLayout nDrawerLayout;
    private ActionBarDrawerToggle nToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //menu action drawer
        nDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        nToggle = new ActionBarDrawerToggle(this,nDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        nDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerButtons();
        initNavigationDrawer();



    }
    // navigation drawer buttons


    public void initNavigationDrawer() {

        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.nav_login:
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        //Toast.makeText(getApplicationContext(),"Name",Toast.LENGTH_SHORT).show();
                        nDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_camera:
                        Intent intentB = new Intent(MainActivity.this,TakePhotoActivity.class);
                        startActivity(intentB);
                        //Toast.makeText(getApplicationContext(),"Name",Toast.LENGTH_SHORT).show();
                        nDrawerLayout.closeDrawers();
                        break;
                }
                return true;
            }


        });
    }

    public void registerButtons(){
        register(R.id.singlePlayerGameBT);
        register(R.id.multiPlayerGameBT);
        register(R.id.highscoresBT);
        register(R.id.creditsBT);
    }

    private void register(int buttonResourceId){
        findViewById(buttonResourceId).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.singlePlayerGameBT:
                    Intent intent = new Intent(MainActivity.this,SinglePlayerActivity.class);
                    startActivity(intent);
                    break;
                case R.id.multiPlayerGameBT:
                    Intent intentB = new Intent(MainActivity.this,MultiplayerOptionsActivity.class);
                    startActivity(intentB);
                    break;
                case R.id.highscoresBT:
                    Toast.makeText(getApplicationContext(),"Button is Working",Toast.LENGTH_LONG).show();
                    break;
                case R.id.creditsBT:
                    Intent intentC = new Intent(MainActivity.this, CreditsActivity.class);
                    startActivity(intentC);
                    break;
                case View.NO_ID:
                default:
                    // TODO Auto-generated method stub
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(nToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
