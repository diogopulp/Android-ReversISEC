package com.example.diogosantos.reversisec.logic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.diogosantos.reversisec.R;

import java.util.ArrayList;

public class Game extends BaseAdapter {

    private static final int TAMROW = 8;
    private static final int TAMCOL = 8;
    private static final int BOARDSIZE = TAMCOL * TAMROW;
    private static final int TOTALPOINTSAVAILABLE = BOARDSIZE;

    private static final int BLACK = R.drawable.ic_reversi_black;
    private static final int WHITE = R.drawable.ic_reversi_white;
    private static final int EMPTY = 0;

    private int screenHeight;
    private int screenWidth;

    private Board board;
    private Player p1, p2;
    private Integer[] mThumbIds;
    private Context mContext;

    private int currentPID;
    private int currentPiece = 0;

    public Game(Context c, int height, int width){

        currentPID = 1;

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

    public int getPid(){
        return currentPID;
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

    public void initGame(){

        setStartUpPlayer(1);

        // Startup pattern
        placePieceAux(27,WHITE);
        placePieceAux(28,BLACK);
        placePieceAux(35,BLACK);
        placePieceAux(36,WHITE);

    }

    public boolean placePiece(int position){

        if (position == -1)
            return false;


        int row = this.positionToRow(position);
        int col = this.positionToCol(position);

        // Check if piece is placed inside the board bounds
        if (col >= 0 && col <= TAMCOL && row >= 0 && row <= TAMROW && col != -1 && row != -1) {

            // Check if the selected place is not yet occupied
            if(board.get(row,col).getImg() ==  EMPTY) {

                // Check if exists al least one piece close by
                if(checkPieceProximity(row,col)) {

                    board.addPiece(row, col, currentPiece);
                    changePID();
                    return true;
                }
            }
        }

        return false;


    }

    public static int getTotalPointsAvailabe() {
        return TOTALPOINTSAVAILABLE;
    }

    public void setStartUpPlayer(int PID){

        if(PID == 1){
            this.currentPID = 1;
            this.currentPiece = BLACK;
        }else if(PID == 2){
            this.currentPID = 2;
            this.currentPiece = WHITE;
        }
    }

    // Aux Private Functions

    private boolean checkPieceProximity(int row, int col){

        ArrayList <Integer> positions;
        positions = new ArrayList<>(8);

        boolean res = false;

        // Top Left Corner
        if(row == 0 && col == 0){

            if(checkPosition(board.get(row + 1, col + 1).getId())) {
                fillDiagonalFrom_BOTTOMRIGHT_to_TOPLEFT(row, col);
                res = true;
            }

            if(checkPosition(board.get(row + 1, col).getId())){
                fillColumnFrom_TOP_to_BOTTOM(row, col);
                res = true;
            }


            if(checkPosition(board.get(row, col + 1).getId())){
                fillRowFrom_LEFT_to_RIGHT(row,col);
                res = true;
            }

            return res;
        }

        // Top Right Corner
        if(row == 0 && col == TAMCOL-1){

            if(checkPosition(board.get(row+1,col-1).getId())){
                fillDiagonalFrom_TOPRIGHT_to_BOTTOMLEFT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row + 1, col).getId())){
                fillColumnFrom_TOP_to_BOTTOM(row, col);
                res = true;
            }

            if(checkPosition(board.get(row, col - 1).getId())){
                fillRowFrom_RIGHT_to_LEFT(row,col);
                res = true;
            }

            return res;
        }

        // Bottom Left Corner
        if(row == TAMROW-1 && col == 0){

            if(checkPosition(board.get(row - 1, col + 1).getId())){
                fillDiagonalFrom_BOTTOMLEFT_to_TOPRIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col).getId())){
                fillColumnFrom_BOTTOM_to_TOP(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col + 1).getId())){
                fillRowFrom_LEFT_to_RIGHT(row,col);
                res = true;
            }

            return res;
        }

        // Botom Right Corner
        if(row == TAMROW-1 && col == TAMCOL-1){

            if(checkPosition(board.get(row - 1, col - 1).getId())){
                fillDiagonalFrom_TOPLEFT_to_BOTTOMRIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col).getId())){
                fillColumnFrom_BOTTOM_to_TOP(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col - 1).getId())){
                fillRowFrom_RIGHT_to_LEFT(row,col);
                res = true;
            }

            return res;
        }

        // Top Row
        if(row == 0 && col != 0 && col != TAMCOL-1) {

            if(checkPosition(board.get(row+1,col-1).getId())){
                fillDiagonalFrom_TOPRIGHT_to_BOTTOMLEFT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row + 1, col + 1).getId())){
                fillDiagonalFrom_BOTTOMRIGHT_to_TOPLEFT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row + 1, col).getId())){
                fillColumnFrom_TOP_to_BOTTOM(row, col);
                res = true;
            }

            if(checkPosition(board.get(row, col + 1).getId())){
                fillRowFrom_LEFT_to_RIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col - 1).getId())){
                fillRowFrom_RIGHT_to_LEFT(row,col);
                res = true;
            }

            return res;

        }

        // Bottom Row
        if(row == TAMROW-1 && col != 0 && col != TAMCOL-1) {

            if(checkPosition(board.get(row - 1, col - 1).getId())){
                fillDiagonalFrom_TOPLEFT_to_BOTTOMRIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col + 1).getId())){
                fillDiagonalFrom_BOTTOMLEFT_to_TOPRIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col).getId())){
                fillColumnFrom_BOTTOM_to_TOP(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col + 1).getId())){
                fillRowFrom_LEFT_to_RIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col - 1).getId())){
                fillRowFrom_RIGHT_to_LEFT(row,col);
                res = true;
            }

            return res;
        }

        // Left Col
        if(row != 0 && col == 0 && row != TAMROW-1) {


            if(checkPosition(board.get(row + 1, col + 1).getId())){
                fillDiagonalFrom_BOTTOMRIGHT_to_TOPLEFT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col + 1).getId())){
                fillDiagonalFrom_BOTTOMLEFT_to_TOPRIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row + 1, col).getId())){
                fillColumnFrom_TOP_to_BOTTOM(row, col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col).getId())){
                fillColumnFrom_BOTTOM_to_TOP(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col + 1).getId())){
                fillRowFrom_LEFT_to_RIGHT(row,col);
                res = true;
            }

            return res;

        }

        // Right Col
        if(row != 0 && col == TAMCOL-1 && row != TAMROW-1) {

            if(checkPosition(board.get(row - 1, col - 1).getId())){
                fillDiagonalFrom_TOPLEFT_to_BOTTOMRIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row+1,col-1).getId())){
                fillDiagonalFrom_TOPRIGHT_to_BOTTOMLEFT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row + 1, col).getId())){
                fillColumnFrom_TOP_to_BOTTOM(row, col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col).getId())){
                fillColumnFrom_BOTTOM_to_TOP(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col - 1).getId())){
                fillRowFrom_RIGHT_to_LEFT(row,col);
                res = true;
            }

            return res;
        }

        // Center
        if(row != 0 && row != TAMROW-1 && col!= 0 && col != TAMCOL-1) {


            if(checkPosition(board.get(row + 1, col + 1).getId())){
                fillDiagonalFrom_BOTTOMRIGHT_to_TOPLEFT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col - 1).getId())){
                fillDiagonalFrom_TOPLEFT_to_BOTTOMRIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row + 1, col - 1).getId())){
                fillDiagonalFrom_TOPRIGHT_to_BOTTOMLEFT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col + 1).getId())){
                fillDiagonalFrom_BOTTOMLEFT_to_TOPRIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row + 1, col).getId())){
                fillColumnFrom_TOP_to_BOTTOM(row, col);
                res = true;
            }

            if(checkPosition(board.get(row - 1, col).getId())){
                fillColumnFrom_BOTTOM_to_TOP(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col + 1).getId())){
                fillRowFrom_LEFT_to_RIGHT(row,col);
                res = true;
            }

            if(checkPosition(board.get(row, col - 1).getId())){
                fillRowFrom_RIGHT_to_LEFT(row,col);
                res = true;
            }

            return res;
        }

        return false;
    }

    private boolean checkPosition(int piece){
        if(piece != EMPTY && piece != currentPID)
            return true;
        return false;

    }
    
    private boolean checkForPieces(ArrayList <Integer> arrayList){

        for(int i = 0; i< arrayList.size(); i++){
            if(arrayList.get(i) != EMPTY && arrayList.get(i) != currentPID)
                return true;
        }
        return false;
    }

    private void fillDiagonalFrom_TOPLEFT_to_BOTTOMRIGHT(int row, int col){

        int j, y;

        j = col - 1;
        y = col - 1;

        for (int i = row-1; i>= 0; i--){

            if(board.get(i,j).getId() == currentPID){

                for (int x = row-1; x> i; x--) {

                    board.addPiece(x, y, currentPiece);
                    if(y>j && y >0 && y <= TAMROW && y<= TAMCOL) {
                        y--;
                    }else{
                        return;
                    }
                }

            }
            if(j>0 && j <= TAMROW && y<= TAMCOL) {
                j--;
            }else{
                return;
            }

        }

    }

    private void fillDiagonalFrom_BOTTOMRIGHT_to_TOPLEFT(int row, int col){

        int j, y;

        j = col + 1;
        y = col + 1;


        for (int i = row+1; i< TAMROW; i++){

            if(board.get(i,j).getId() == currentPID){

                for (int x = row + 1; x< i; x++) {

                    board.addPiece(x, y, currentPiece);

                    if(y<j-1 && y < TAMROW-1 && y< TAMCOL-1) {
                        y++;
                    }else{
                        return;
                    }
                }

            }
            if(j < TAMROW-1 && j< TAMCOL-1) {
                j++;
            }else{
                return;
            }

        }

    }

    private void fillDiagonalFrom_TOPRIGHT_to_BOTTOMLEFT(int row, int col){

        // TODO Encontrar bug neste pedaço de lógica
        int j, y;

        j = col - 1;
        y = col - 1;


        // Increase the value of rows
        for (int i = row+1; i< TAMROW; i++){

            if(board.get(i,j).getId() == currentPID){

                // Fill With Pieces
                for (int x = row + 1; x< i; x++) {

                    board.addPiece(x, y, currentPiece);

                    if(y > j-1 && y >= 0 && y < TAMROW-1) {
                        y--;
                    }else{
                        return;
                    }
                }

            }
            // Decrease the value of columns
            if(j >= 0 && j< TAMCOL-1) {
                j--;
            }else{
                return;
            }

        }

    }

    private void fillDiagonalFrom_BOTTOMLEFT_to_TOPRIGHT(int row, int col){

        int j, y;

        j = col + 1;
        y = col + 1;


        // Decrease the value of rows
        for (int i = row-1; i< TAMROW; i--){

            if(board.get(i,j).getId() == currentPID){

                // Fill With Pieces
                for (int x = row - 1; x> i; x--) {

                    board.addPiece(x, y, currentPiece);

                    if(y < j+1 && y >= 0 && y < TAMROW-1) {
                        y++;
                    }else{
                        return;
                    }
                }

            }
            // Increase the value of columns
            if(j >= 0 && j< TAMROW-1 && j< TAMCOL-1) {
                j++;
            }else{
                return;
            }

        }

    }

    private void fillColumnFrom_TOP_to_BOTTOM(int row, int col){

        // Increase the value of rows
        for (int i = row+1; i< TAMROW; i++){

            if(board.get(i,col).getId() == currentPID){

                // Fill With Pieces
                for (int x = row + 1; x< i; x++) {

                    board.addPiece(x, col, currentPiece);

                }
                return;

            }

        }

    }

    private void fillColumnFrom_BOTTOM_to_TOP(int row, int col){

        // Decrease the value of rows
        for (int i = row-1; i> 0; i--){

            if(board.get(i,col).getId() == currentPID){

                // Fill With Pieces
                for (int x = row - 1; x> i; x--) {

                    board.addPiece(x, col, currentPiece);

                }
                return;

            }

        }

    }

    private void fillRowFrom_LEFT_to_RIGHT(int row, int col){

        // Increase the value of rows
        for (int i = col+1; i< TAMCOL; i++){

            if(board.get(row,i).getId() == currentPID){

                // Fill With Pieces
                for (int x = col + 1; x< i; x++) {

                    board.addPiece(row, x, currentPiece);

                }
                return;

            }

        }

    }

    private void fillRowFrom_RIGHT_to_LEFT(int row, int col){

        // Decrease the value of rows
        for (int i = col-1; i> 0; i--){

            if(board.get(row,i).getId() == currentPID){

                // Fill With Pieces
                for (int x = col - 1; x> i; x--) {

                    board.addPiece(row, x, currentPiece);

                }
                return;

            }

        }

    }

    private void placePieceAux(int position, int color){

        int row = this.positionToRow(position);
        int col = this.positionToCol(position);

        if (col >= 0 && col <= TAMCOL && row >=0 && row <= TAMROW && col != -1 && row != -1) { // Check valid input

            board.addPiece(row, col, color);
        }
    }

    private void changePieces(){

        if(currentPID == 1){
            currentPiece = BLACK;
        }else if(currentPID == 2){
            currentPiece = WHITE;
        }
    }

    private void changePID(){

        if(currentPID == 1) {
            currentPID = 2;
        }else{
            currentPID = 1;
        }

        changePieces();
    }




}
