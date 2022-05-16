package com.example.tutorial;

import java.util.Vector;

public class Sector {
    int numOfBlocks;
    boolean isReadable;
    String accessKey;
    Vector<Block> blocks = new Vector<>();

    //To create different sectors with different block sizes.
    //can have multiple blocks.
    //constructor creates a single sector.

    //boolean if was able to access using key. Store the key;
    public Sector(int b){
        this.numOfBlocks = b;
    }
    public void addBlock(Block block){
        this.blocks.add(block);
    }
    public void setIsReadable(boolean isReadable){
        this.isReadable = isReadable;
    }
    public void setAccessKey(String accessKey){
        this.accessKey = accessKey;
    }

    public String getAccessKey(){
        return this.accessKey;
    }
    public int getNumOfBlocks(){
        return  this.numOfBlocks;
    }

    public Vector<Block> getBlocks(){
        return this.blocks;
    }

}
