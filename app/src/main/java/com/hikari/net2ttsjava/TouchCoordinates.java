package com.hikari.net2ttsjava;

class TouchCoordinates{
    int x = 0;
    int y = 0;
    public TouchCoordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "\"x\": "+this.x+", \"y\": "+y;
    }
}