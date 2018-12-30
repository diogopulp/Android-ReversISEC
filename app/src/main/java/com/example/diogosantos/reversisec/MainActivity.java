package com.example.diogosantos.reversisec;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.strictmode.NonSdkApiUsedViolation;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.diogosantos.reversisec.multiplayer.MultiplayerOptionsActivity;
import com.example.diogosantos.reversisec.singleplayer.SinglePlayerActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btn_take_photo;
    private DrawerLayout nDrawerLayout;
    private ActionBarDrawerToggle nToggle;
    private ImageButton profileImgButon;

    //Google Login
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";
    private FirebaseAuth mAuth;

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


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void updateUI(GoogleSignInAccount acct){
        //GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

        if (acct != null) {

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Log.i("NAME: ", personName);
        }
    }


    public void initNavigationDrawer() {

        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Google Sign In

        //profileImgButon = findViewById(R.id.ibtn_profile_photo);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        /*profileImgButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"ola",Toast.LENGTH_LONG).show();
                //signIn();
            }
        });*/


        View headerview = navigationView.getHeaderView(0);
        profileImgButon = headerview.findViewById(R.id.ibtn_profile_photo);

        profileImgButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.ibtn_profile_photo:
                        Toast.makeText(MainActivity.this,"ola",Toast.LENGTH_LONG).show();
                        signIn();
                        break;

                    /*case R.id.nav_login:
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        //Toast.makeText(getApplicationContext(),"Name",Toast.LENGTH_SHORT).show();
                        nDrawerLayout.closeDrawers();
                        break;*/
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
