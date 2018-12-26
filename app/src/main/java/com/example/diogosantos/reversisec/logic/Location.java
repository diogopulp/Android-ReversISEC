package com.example.diogosantos.reversisec.logic;

import com.example.diogosantos.reversisec.R;

public class Location {

    private int img;
    private int id; // 0 Empty, 1 P1, 2 P2

    private static final int BLACK = R.drawable.ic_reversi_black;
    private static final int WHITE = R.drawable.ic_reversi_white;

    public Location(){
        setID(0);
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;

        if(this.img == BLACK)
            setID(1);
        if(this.img == WHITE)
            setID(2);
    }

    public int getId() {
        return id;
    }

    // Private Aux Methods
    private void setID(int id) {

        // 0 Empty, 1 P1, 2 P2

        if(id >= 0 && id <=3){
            this.id = id;
        }

    }
}
