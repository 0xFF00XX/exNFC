package com.example.tutorial;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import java.util.Vector;

public class NFCData
{

    //FIELDS
    int size,sectorCount,blockCount;
    int id;
    String hexID;
    String type = "Unknown";
    Vector<String> techs = new Vector<String>();
    Vector<String> keys = new Vector<>();

    Sector[] sectors;


    //
    //GETTERS AND SETTERS
    //
    //KEYS
    public NFCData(){

    }
    public void setKey(String key){
        this.keys.add(key);
    }
    public String getKey(int s){
//        String result = "";
//        Log.i("SAMPLE", "" + keys.elementAt(s)[0]);

        return keys.elementAt(s);
    }
    //IDs
    public String getId() {
        return Integer.toString(id);
    }
    public void setId(int id) {
        this.id = id;
    }

    //ID HEXED
    public String getHexID() {
        return hexID;
    }
    public void setHexID(String hexID) {
        this.hexID = hexID;
    }

    //SIZE
    public String getSize() {
        return Integer.toString(size);
    }
    public void setSize(int size) {
        this.size = size;
    }

    //SECTOR COUNT
    public String getSectorCount() {
         return Integer.toString(sectorCount);
    }
    public void setSectorCount(int sectorCount) {
        this.sectorCount = sectorCount;
    }

    //BLOCK COUNT
    public String getBlockCount() {
        return Integer.toString(blockCount);
    }
    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    //TYPE
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    //TECHNOLOGIES
    public Vector<String> getTechs() {
        return techs;
    }
    public void addTech(String tech){
        if(!this.techs.contains(tech)){
          //  Log.i("TECH", "added " + tech);
            this.techs.add(tech);
        }

    }

    //SECTORS
    public void setSectors(Sector[] sectors){
        this.sectors = sectors;
    }
    public Sector[] getSectors() {
        return sectors;
    }






//    public void addSectors(Sector sector){
//        //check length?
//        if(this.sectors.size() <= Integer.valueOf(this.getSectorCount())){
//            this.sectors.add(sector);
//        }
//        //else duplicated?
//    }
//    public Vector<Sector> getSectors(){
//        return this.sectors;
//    }
    public void setAccess(int s, int i){
//        accessableSectors.add();
        ;
    }
}
