package com.example.diogosantos.reversisec.logic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.diogosantos.reversisec.R;

public class Game extends BaseAdapter {

    private static final int TAMROW = 8;
    private static final int TAMCOL = 8;
    private static final int BOARDSIZE = TAMCOL * TAMROW;
    private static final int TOTALPOINTSAVAILABLE = BOARDSIZE;

    private int screenHeight;
    private int screenWidth;

    private Board board;
    private Player p1, p2;
    private Integer[] mThumbIds;
    private Context mContext;

    private int previousPiece = 0;

    public Game(Context c, int height, int width){

        this.screenHeight = height;
        this.screenWidth = width;

        board = new Board();
        mThumbIds = new Integer[BOARDSIZE];

        // Clean the board
        for (int i=0;i<BOARDSIZE;i++)
            mThumbIds[i] = 0;

        mContext = c;

        updateView(this.board);

    }

    public void updateView(Board board){

        int k=0;
        for (int i=0; i< TAMROW; i++){
            for(int j = 0; j< TAMCOL; j++) {
                mThumbIds[k] = board.get(i,j).getImg();
                k++;
            }
        }
    }

    public Board getBoard(){
        return this.board;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        updateView(this.board);

        double width, height;

        if(screenHeight <= 900) { // Low DPI
            width = screenWidth / 9;
            height = screenHeight / 16.5;
        }else if(screenHeight > 900 && screenHeight < 1300){ // Medium DPI
            width = screenWidth / 9;
            height = screenHeight / 14.5;
        }else if(screenHeight >= 1300 && screenHeight < 1800){ // High DPI
            width = screenWidth / 9;
            height = screenHeight / 14.3;
        }else if(screenHeight >= 1800 && screenHeight < 2000){
            width = screenWidth / 9;
            height = screenHeight / 14.8;
        }else{
            width = screenWidth / 9;
            height = screenHeight / 13.5;
        }

        if (view == null) {


            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((int)width, (int)height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 5, 0, 0);

        } else {
            imageView = (ImageView) view;
        }

        try {
            imageView.setImageResource(mThumbIds[position]);
            //imageView.setImageResource(playerBoard.get(positionToRow(position),positionToRow(position)).getImg());
        }
        catch (Exception e) {

            Log.e("Erro ", e.getLocalizedMessage());

        }
        return imageView;
    }

    public int positionToRow(int position){

        int row = 0;

        if(position >= 0 && position <= 7)
            row = 0;
        if(position >= 8 && position <= 15)
            row = 1;
        if(position >= 16 && position <= 23)
            row = 2;
        if(position >= 24 && position <= 31)
            row = 3;
        if(position >= 32 && position <= 39)
            row = 4;
        if(position >= 40 && position <= 47)
            row = 5;
        if(position >= 48 && position <= 55)
            row = 6;
        if(position >= 56 && position <= 63)
            row = 7;

        return row;
    }

    public int positionToCol(int position){

        int col = 0;

        if(position == 0 || position == 8 || position == 16 || position == 24 || position == 32 || position == 40 || position == 48 || position == 56)
            col = 0;
        if(position == 1 || position == 9 || position == 17 || position == 25 || position == 33 || position == 41 || position == 49 || position == 57)
            col = 1;
        if(position == 2 || position == 10 || position == 18 || position == 26 || position == 34 || position == 42 || position == 50 || position == 58)
            col = 2;
        if(position == 3 || position == 11 || position == 19 || position == 27 || position == 35 || position == 43 || position == 51 || position == 59)
            col = 3;
        if(position == 4 || position == 12 || position == 20 || position == 28 || position == 36 || position == 44 || position == 52 || position == 60)
            col = 4;
        if(position == 5 || position == 13 || position == 21 || position == 29 || position == 37 || position == 45 || position == 53 || position == 61)
            col = 5;
        if(position == 6 || position == 14 || position == 22 || position == 30 || position == 38 || position == 46 || position == 54 || position == 62)
            col = 6;
        if(position == 7 || position == 15 || position == 23 || position == 31 || position == 39 || position == 47 || position == 55 || position == 63)
            col = 7;

        return col;

    }

    public boolean placePiece(int position){

        int row = -1;
        int col = -1;

        row = this.positionToRow(position);
        col = this.positionToCol(position);

        if (col >= 0 && col <= TAMCOL && row != -1) { // Check valid input

            if(previousPiece == R.drawable.ic_reversi_white || previousPiece == 0) {
                board.addPiece(row, col, R.drawable.ic_reversi_black);
                previousPiece = R.drawable.ic_reversi_black;
            }else if(previousPiece == R.drawable.ic_reversi_black) {
                board.addPiece(row, col, R.drawable.ic_reversi_white);
                previousPiece = R.drawable.ic_reversi_white;
            }

            return true;
        }

        return false;


    }

    public static int getTotalPointsAvailabe() {
        return TOTALPOINTSAVAILABLE;
    }
}
