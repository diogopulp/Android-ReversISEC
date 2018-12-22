package com.example.diogosantos.reversisec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.diogosantos.reversisec.logic.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.diogosantos.reversisec.utils.Utils.*;

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

    TextView myTextViewP1;
    TextView myTextViewP2;
    TextView myTextViewP1number;
    TextView myTextViewP2number;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (getIntent().getExtras() != null) {
            if (this.getIntent().getExtras().containsKey("GAME_TYPE")) {
                isSinglePlayer = this.getIntent().getExtras().getBoolean("GAME_TYPE");
            } else {
                Log.e("GameActivity", "Missing game type");
                finish();
            }
            if (this.getIntent().getExtras().containsKey("GAME_MODE")) {
                mode = this.getIntent().getExtras().getInt("GAME_MODE");
            }
        } else {
            Log.e("GameActivity", "Extras are NULL");
            finish();
        }

        //Lança cada tipo de jogo

        if (isSinglePlayer) {
            launchSinglePlayer();
        } else {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Toast.makeText(this, R.string.app_ip, Toast.LENGTH_LONG).show(); // TODO Meter isto na biblioteca de strings
                finish();
                return;
            }

            Intent intent = getIntent();
            // if (intent != null)
            // mode = intent.getIntExtra("mode", SERVER);

            procMsg = new Handler();
            launchMultiPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isSinglePlayer) {
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
        pd.setTitle(R.string.app_Connection);                          //
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
                    String[] separated = read.split(",");
                    //final int oldPosition = Integer.parseInt(separated[0]);
                    //final int nextPosition = Integer.parseInt(separated[1]);
                    Log.d("Multiplayer", "received a move: " + position);
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
        edtIP.setText("192.168.0.111"); // most places default ip
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
        game = new Game(this, deviceHeight, deviceWidth);

        gridView = findViewById(R.id.playBoard);
        gridView.setAdapter(game);
    }

    private void launchSinglePlayer() {

        setupAdapter();

        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                int currentXPos = (int) event.getX();
                int currentYPos = (int) event.getY();

                position = gridView.pointToPosition(currentXPos, currentYPos);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    game.placePiece(position);
                    Toast.makeText(GameActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                    game.notifyDataSetChanged();
                }

                return false;
            }
        });

    }

    private void launchMultiPlayer() {

        setupAdapter();

        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int currentXPos = (int) event.getX();
                int currentYPos = (int) event.getY();


                position = gridView.pointToPosition(currentXPos, currentYPos); // Converte coordenadas para a posição da Gridview

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    vibrate();

                    game.placePiece(position);
                    sendMessage(position);

                    /*if (result == -2) // erro, posiçao nao valida
                        Toast.makeText(GameActivity.this, "Movimento inválido!", Toast.LENGTH_SHORT).show(); // TODO Adicionar à biblioteca de strings
                    else if (result == -1) {
                        oldPosition = position;
                    } else {
                        //if (oldPosition != -1)
                        sendMessage(oldPosition, position);
                    }*/
                }

                game.notifyDataSetChanged();
                //changePlayer(game.);
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
        myTextViewP1number = (TextView) findViewById(R.id.player1TVnumber);
        myTextViewP2number = (TextView) findViewById(R.id.player2TVnumber);
        myTextViewP1.setVisibility(View.VISIBLE);
        myTextViewP2.setVisibility(View.INVISIBLE);
        myTextViewP1number.setVisibility(View.VISIBLE);
        myTextViewP2number.setVisibility(View.INVISIBLE);
        gridView = findViewById(R.id.playBoard);
        gridView.setAdapter(game);
    }

    public void changePlayer(int id) {

        if (id == 2) {
            myTextViewP1.setVisibility(View.INVISIBLE);
            myTextViewP2.setVisibility(View.VISIBLE);
            myTextViewP1number.setVisibility(View.INVISIBLE);
            myTextViewP2number.setVisibility(View.VISIBLE);
            return;
        } else if (id == 1) {
            myTextViewP2.setVisibility(View.INVISIBLE);
            myTextViewP1.setVisibility(View.VISIBLE);
            myTextViewP2number.setVisibility(View.INVISIBLE);
            myTextViewP1number.setVisibility(View.VISIBLE);
            return;
        }
    }

    public void vibrate() {

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(20);

    }
}
