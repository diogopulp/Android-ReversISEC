package com.example.diogosantos.reversisec.logic;

public class Location {

    private int img;
    private int id; // 0 Empty, 1 P1, 2 P2

    public Location(){

    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        // 0 Empty, 1 P1, 2 P2

        if(id >= 0 && id <=3){
            this.id = id;
        }

    }
}
