package com.example.diogosantos.reversisec.logic;

public class Board {

    public static final int NUM_ROWS = 8;
    public static final int NUM_COLS = 8;

    private Location[][] board;
    private Location[] transitionBoard;

    public Board()
    {
        board = new Location[NUM_ROWS][NUM_COLS];
        transitionBoard = new Location[NUM_ROWS*NUM_COLS];

        for(int i = 0; i<transitionBoard.length; i++){
            Location tempLoc = new Location();
            transitionBoard[i] = tempLoc;
        }

        for (int row = 0; row < board.length; row++)
        {
            for (int col = 0; col < board[row].length; col++)
            {
                Location tempLoc = new Location();
                board[row][col] = tempLoc;
            }
        }


    }
    public Location[][] getLoctionBoard(){
        return board;
    }

    public Location[] getTransitionBoard() {
        return transitionBoard;
    }

    public Location get(int row, int col)
    {
        return board[row][col];
    }

    public void addPiece(int row, int col, int pieceID){

        board[row][col].setImg(pieceID);
        //transitionBoard[]

    }

}
