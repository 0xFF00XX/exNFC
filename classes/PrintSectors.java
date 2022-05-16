package com.example.tutorial;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

public class PrintSectors {
    LinearLayout sectorArea, blockArea;
    Vector<Button> sectorBtns;
    NFCData tagData;
    Context context;
    public PrintSectors(Context context,  LinearLayout sectorArea, LinearLayout blockArea, NFCData tag){
        this.sectorArea = sectorArea;
        this.blockArea = blockArea;
        this.tagData = tag;
        this.context = context;
        sectorBtns = new Vector<>();
//        this.tagData =
    }
    public void printAllSectors(){
        //4k
        for(int i=0; i<tagData.sectors.length; i++){
            //init and configure sector buttons.
            Button btn = new Button(context);
            sectorBtns.add(new Button(context));
            sectorBtns.elementAt(i).setText("Sector " + i);
            sectorBtns.elementAt(i).setWidth(300);
            sectorBtns.elementAt(i).setHeight(100);
            sectorBtns.elementAt(i).setPadding(0,0,0,0);
            //set colour
            if(tagData.getSectors()[i].isReadable){
                sectorBtns.elementAt(i).setBackgroundColor(Color.GREEN);
            }
            else{
                sectorBtns.elementAt(i).setBackgroundColor(Color.RED);
            }
            //final int to use in inner listener
            final int sectorInt = i;
            sectorBtns.elementAt(i).setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //change all others to green or red
                    for (int bt = 0; bt<sectorBtns.size();bt++){
                        if(tagData.getSectors()[bt].isReadable){
                            sectorBtns.elementAt(bt).setBackgroundColor(Color.GREEN);
                        }
                        else{
                            sectorBtns.elementAt(bt).setBackgroundColor(Color.RED);
                        }
                    }


                    sectorBtns.elementAt(sectorInt).setBackgroundColor(Color.YELLOW);
                    blockArea.removeAllViews();
                    TextView wromgData = new TextView(context);
                    if(tagData.getSectors()[sectorInt].isReadable == false){
                        wromgData.setText("Not Readable. Wrong Key?");
                        blockArea.addView(wromgData);
                    }
                    else{
                        for(int j = 0; j<tagData.getSectors()[sectorInt].getBlocks().size(); j++){
                            //print blocks

                            TextView blockdata = new TextView(context);
                            String stringBlock = "";
                            stringBlock+="Block " + j + "\n";
                            for(int k = 0;k<tagData.getSectors()[sectorInt].getBlocks().elementAt(j).getData().length;k++){
                                stringBlock += String.format("%02X", tagData.getSectors()[sectorInt].getBlocks().elementAt(j).getData()[k] & 0xff) + " ";
                                if(k == tagData.getSectors()[sectorInt].getBlocks().elementAt(j).getData().length/2-1){
                                    stringBlock+="\n";
                                }
                                // stringBlock += scannedTag.getSectors()[sectorInt].getBlocks().size();
                            }

                            blockdata.setText(stringBlock);
                            blockdata.setTextIsSelectable(true);

                            blockArea.addView(blockdata);
                        }

                        TextView keyText = new TextView(context);
                        keyText.setText("\n\nKey = "  + tagData.sectors[sectorInt].getAccessKey());
                        blockArea.addView(keyText);
                        //add checkbox for bruteforce?

                    }
                }
            });
            sectorArea.addView(sectorBtns.elementAt(i));
        }

    }

}
