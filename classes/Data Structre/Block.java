package com.example.tutorial;

public class Block {
    //stores array of bytes
    // example : {C3 91 2C 13 6D 08 04 00 62 63 64 65 66 67 68 69 }
    byte [] data;
    int index;
    public Block(byte[] b, int index){
        this.data = b;
        this.index = index;
    }
    public byte[] getData(){
        return this.data;
    }
    public int getIndex(){
        return this.index;
    }
}
