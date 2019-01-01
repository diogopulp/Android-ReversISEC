package com.example.diogosantos.reversisec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diogosantos.reversisec.logic.Game;

import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static com.example.diogosantos.reversisec.utils.Utils.getLocalIpAddress;

public class GameActivity extends AppCompatActivity {

    public static final int SERVER = 0;
    public static final int CLIENT = 1;
    public static final int ME = 0;
    public static final int OTHER = 1;

    int PID;

    static final String GAME_STATE_KEY = "game";
    //String mGameState;

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
    boolean isMultiPlayerDevice;

    TextView myTextViewP1;
    TextView myTextViewP2;
    TextView myTextViewP1number;
    TextView myTextViewP2number;
    TextView myTextViewCPU;
    ImageView myImageViewP1;
    ImageView myImageViewP2;


    DisplayMetrics displayMetrics;

    int height, width;
    int position;

    //View
    private GridView gridView;

    //Game
    private String gameType;
    private Game game;

    //Device
    private int deviceHeight;
    private int deviceWidth;


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        //outState.putParcelableArrayList(GAME_STATE_KEY,game.getBoard().getLoctionBoard());
        //outState.putParcelableArray(GAME_STATE_KEY, game.getBoard().getTransitionBoard());

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //mTextView.setText(savedInstanceState.getString(TEXT_VIEW_KEY));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //game = savedInstanceState.getParcelable(GAME_STATE_KEY);
        }

        setContentView(R.layout.activity_game);

        if (getIntent().getExtras() != null) {
            if (this.getIntent().getExtras().containsKey("GAME_SINGLE")) {
                isSinglePlayer = this.getIntent().getExtras().getBoolean("GAME_SINGLE");

            } else if (this.getIntent().getExtras().containsKey("GAME_MULTI_ONE_DEVICE")) {
                isMultiPlayerDevice = this.getIntent().getExtras().getBoolean("GAME_MULTI_ONE_DEVICE");

            } else if (this.getIntent().getExtras().containsKey("GAME_MULTI_MODE")) {
                mode = this.getIntent().getExtras().getInt("GAME_MULTI_MODE");

            }
        } else {
            Log.e("GameActivity", "Extras are NULL");
            finish();
        }
        //Lança cada tipo de jogo
        if (isSinglePlayer) {
            launchSinglePlayer();
        } else if (isMultiPlayerDevice) {
            launchMultiPlayerOneDevice();
        } else {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Toast.makeText(this, R.string.app_ip, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            procMsg = new Handler();
            launchMultiPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isSinglePlayer && !isMultiPlayerDevice) {
            if (mode == SERVER)
                server();
            else  // CLIENT
                clientDlg();
        }
    }

    // Comunication Methods

    void client(final String strIP, final int Port) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("Socket Client", R.string.app_connectServer + strIP);
                    socketGame = new Socket(strIP, Port);

                    // Início do Jogo
                    PID = 2;


                } catch (Exception e) {
                    socketGame = null;
                }
                if (socketGame == null) {
                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                    return;
                }
                commThread.start();

            }
        });
        t.start();
    }

    void server() {

        String ip = getLocalIpAddress();
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.app_ip) + "\n(IP: " + ip + ")"); // @TODO Meter isto na biblioteca de strings
        pd.setTitle(R.string.app_Connection);                             //
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                    }
                    serverSocket = null;
                }
            }
        });
        pd.show();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    socketGame = serverSocket.accept();
                    serverSocket.close();
                    serverSocket = null;
                    commThread.start();

                    // Início do Jogo
                    PID = 1;

                } catch (Exception e) {
                    e.printStackTrace();
                    socketGame = null;
                }
                procMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        if (socketGame == null)
                            finish();
                    }
                });
            }
        });
        t.start();
    }

    Thread commThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {

                input = new BufferedReader(new InputStreamReader(socketGame.getInputStream()));
                output = new PrintWriter(socketGame.getOutputStream());

                while (!Thread.currentThread().isInterrupted()) {
                    String read = input.readLine();
                    //String[] separated = read.split(",");
                    //final int oldPosition = Integer.parseInt(separated[0]);
                    //final int nextPosition = Integer.parseInt(separated[1]);
                    position = Integer.parseInt(read);
                    Log.d("Multiplayer", "Received a move: " + position + "READ:" + read);

                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            //moveOtherPlayer(checkMove);
                            //adapter.getBoard().movePiece(oldPosition, nextPosition);
                            game.placePiece(position);
                            //Tem de ser chamado na UI Thread!
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    game.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {
                procMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        //Toast.makeText(getApplicationContext(), R.string.game_finished, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    });

    void clientDlg() {
        final EditText edtIP = new EditText(this);
        edtIP.setText("192.168.1.183"); // most places default ip
        edtIP.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        AlertDialog ad = new AlertDialog.Builder(this).setTitle(" Client")
                .setMessage("Server IP").setView(edtIP)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client(edtIP.getText().toString(), PORT); // to test with emulators: PORTaux);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                }).create();
        ad.show();
    }

    // Other Methods

    public void setupAdapter(){
        prepareViews();

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;

        gridView = findViewById(R.id.playBoard);
        game = new Game(this, deviceHeight, deviceWidth);
        game.initGame();
        gridView.setAdapter(game);
    }

    private void launchSinglePlayer() {

        setupAdapter();
        game.notifyDataSetChanged();

        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) { // espera que o utilizador selecione uma peça

                int currentXPos = (int) event.getX();
                int currentYPos = (int) event.getY();

                position = gridView.pointToPosition(currentXPos, currentYPos);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(!game.placePiece(position))
                        Toast.makeText(GameActivity.this, "Local Inválido", Toast.LENGTH_SHORT).show();
                    else {
                        while (true) {
                            if (game.placePiece(positionForCPU())) {


                                break;
                            }
                        }
                    }

                    game.notifyDataSetChanged();
                    changePlayerOnSingleVsCPU();
                }
                //metodo Joga CPU
                return false;
            }
        });
    }

    private void fazPip(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 1000);
    }

    private void launchMultiPlayerOneDevice() {

        setupAdapter();
        game.notifyDataSetChanged();

        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                int currentXPos = (int) event.getX();
                int currentYPos = (int) event.getY();

                position = gridView.pointToPosition(currentXPos, currentYPos);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(!game.placePiece(position))
                        Toast.makeText(GameActivity.this, "Local Inválido", Toast.LENGTH_SHORT).show();
                    game.notifyDataSetChanged();
                    changePlayerOnMultiplayer ();
                }
                return false;
            }
        });

    }

    private void launchMultiPlayer() {

        setupAdapter();
        game.notifyDataSetChanged();


        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int currentXPos = (int) event.getX();
                int currentYPos = (int) event.getY();


                position = gridView.pointToPosition(currentXPos, currentYPos); // Converte coordenadas para a posição da Gridview

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    vibrate();

                    // Se for a vez do jogador
                    if(game.getPid() == PID) {

                        // Se for um movimento válido
                        if (game.placePiece(position)) {

                            //String json = "{position: " + position + "}";

                            sendMessage(position);
                        } else {
                            Toast.makeText(GameActivity.this, R.string.invalid_location, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(GameActivity.this, R.string.wait_for_your_turn, Toast.LENGTH_SHORT).show();
                    }
                }

                game.notifyDataSetChanged();
                changePlayerOnMultiplayer();
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }

        });

    }

    void sendMessage(final int position) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("Multiplayer", "Sending a move: " + position);

                    output.println(position);
                    output.flush();
                } catch (Exception e) {
                    Log.d("Multiplayer", "Error sending a move");
                }
            }
        });
        t.start();

    }


    public void prepareViews(){
        myTextViewP1 = (TextView) findViewById(R.id.player1TV);
        myTextViewP2 = (TextView) findViewById(R.id.player2TV);
        myTextViewCPU = (TextView)findViewById(R.id.CPUTV);
        myTextViewP1number = (TextView) findViewById(R.id.player1TVnumber);
        myTextViewP2number = (TextView) findViewById(R.id.player2TVnumber);
        myImageViewP1 = (ImageView)findViewById(R.id.player1img);
        myImageViewP2 = (ImageView)findViewById(R.id.player2img);
        myImageViewP1.setVisibility(View.VISIBLE);
        myImageViewP2.setVisibility(View.INVISIBLE);
        myTextViewP1.setVisibility(View.VISIBLE);
        myTextViewP2.setVisibility(View.INVISIBLE);
        myTextViewP1number.setVisibility(View.VISIBLE);
        myTextViewP2number.setVisibility(View.INVISIBLE);
        gridView = findViewById(R.id.playBoard);
        gridView.setAdapter(game);
    }

    private void changePlayerOnMultiplayer (){
        if(game.getPid()==1){
            myImageViewP1.setVisibility(View.VISIBLE);
            myImageViewP2.setVisibility(View.INVISIBLE);
            myTextViewP1.setVisibility(View.VISIBLE);
            myTextViewP2.setVisibility(View.INVISIBLE);
            myTextViewP1number.setVisibility(View.VISIBLE);
            myTextViewP2number.setVisibility(View.INVISIBLE);
            myTextViewCPU.setVisibility(View.INVISIBLE);
            return;
        }
        else {
            myImageViewP1.setVisibility(View.INVISIBLE);
            myImageViewP2.setVisibility(View.VISIBLE);
            myTextViewP1.setVisibility(View.INVISIBLE);
            myTextViewP2.setVisibility(View.VISIBLE);
            myTextViewP1number.setVisibility(View.INVISIBLE);
            myTextViewP2number.setVisibility(View.VISIBLE);
            myTextViewCPU.setVisibility(View.INVISIBLE);
            return;
        }
    }

    private void changePlayerOnSingleVsCPU (){
        if(game.getPid()==1){
            myImageViewP1.setVisibility(View.VISIBLE);
            myImageViewP2.setVisibility(View.INVISIBLE);
            myTextViewP1.setVisibility(View.VISIBLE);
            myTextViewP2.setVisibility(View.INVISIBLE);
            myTextViewP1number.setVisibility(View.INVISIBLE);
            myTextViewP2number.setVisibility(View.INVISIBLE);
            myTextViewCPU.setVisibility(View.INVISIBLE);
            return;
        }
        else {
            myImageViewP1.setVisibility(View.INVISIBLE);
            myImageViewP2.setVisibility(View.VISIBLE);
            myTextViewP1.setVisibility(View.INVISIBLE);
            myTextViewP2.setVisibility(View.INVISIBLE);
            myTextViewP1number.setVisibility(View.INVISIBLE);
            myTextViewP2number.setVisibility(View.INVISIBLE);
            myTextViewCPU.setVisibility(View.VISIBLE);
            return;
        }
    }

    private int positionForCPU (){
        return (int)(Math.random() * 63 + 0);
    }

    public void vibrate() {

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(20);

    }
}
