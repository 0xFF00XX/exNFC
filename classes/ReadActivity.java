package com.example.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.*;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

public class ReadActivity extends AppCompatActivity {


    //layouts
    ConstraintLayout nfcAlertLayout, mainLayout;
    Vector<String> genericData;
    NfcAdapter adapter;
    boolean wasRead = false;
    boolean toBruteforce = false;
    Sector[] sectors;
    TextView text;
    String[][] techListsArray;
    IntentFilter[] intentFiltersArray;
    PendingIntent pendingIntent;
    int count = 0;
    Button resetBtn,bruteBtn;
    TextView idText,idHexText,sizeText,sectorText,blockText,techText,typeText,sector0Text;
    TableLayout table;
    TextView progress;
    LinearLayout sectorArea;
    LinearLayout blockArea;
    NFCData scannedTag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.read);
        setContentView(R.layout.tag_data);

        adapter = NfcAdapter.getDefaultAdapter(this);
        progress = findViewById(R.id.bruteProgress);
        if(adapter == null){
            //plane with text of unavailability. quitting app
            Toast.makeText(this, "NFC not available on this device", Toast.LENGTH_LONG).show();
            finish();
        }
        if(!adapter.isEnabled()){
            //make alert visible.
            nfcAlertDialog().show();




        }
        bruteBtn = findViewById(R.id.bruteBtn);
        bruteBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                bruteBtn.setBackgroundColor(getResources().getColor(R.color.purple_200));

                if (!toBruteforce){
                    toBruteforce = true;
                    bruteBtn.setBackgroundColor(Color.MAGENTA);
                }
                else{
                    toBruteforce = false;
                }
                Log.i("Brute?",""+toBruteforce);
            }
        });

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);




//
//
//        try {
//            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
//                                       You should specify only the ones that you need. */
//        }
//        catch (IntentFilter.MalformedMimeTypeException e) {
//            throw new RuntimeException("fail", e);



//


    }
//        mainEdit.getText().toString();

    public void onPause() {
        super.onPause();
        if(adapter != null)
            adapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(wasRead) {
            clearScreen();
        }
        setIntent(intent);
        resolveIntent(intent);

//        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        //do something with tagFromIntent
//        text.setText("ID of the tag:" + tagFromIntent.getId() + "\n" + tagFromIntent.toString());
    }

    public AlertDialog nfcAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("NFC is disabled!")
                .setTitle("Error")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                })
                .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                });
        AlertDialog dialog = builder.create();
        return dialog;
    }



    public void resolveIntent(Intent intent){
        String action = intent.getAction();
        //text.setText("Got tag");
        if(adapter.ACTION_TAG_DISCOVERED.equals(action) || adapter.ACTION_TECH_DISCOVERED.equals(action) || adapter.ACTION_NDEF_DISCOVERED.equals(action)){
            Tag tag = (Tag) intent.getParcelableExtra(adapter.EXTRA_TAG);
            assert tag != null;
            //byte[] paylaod = detectTagData(tag).getBytes();
//            text.setText(new String(detectTagData(tag).getBytes()));
            //assign text views


            //if mifare classic
            String prefix = "android.nfc.tech.";
            for(String tech : tag.getTechList()){
//                Log.i("TECHS", tech);
                if(tech.equals("android.nfc.tech.IsoDep") ){
                    Log.i("Type", tech.substring(prefix.length()));
                    detectIsoDep(tag);
                }
                else if(tech.equals("android.nfc.tech.MifareClassic")){
                    Log.i("Type", "Classic");
                    detectTagData(tag);
                }
            }

            //detectTagData(tag);


            resetBtn = findViewById(R.id.resetBtn);
            resetBtn.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //// TODO: 05/15/2022 clear screen, reset all
                    clearScreen();
                }
            });

            //set text to fields.
            if(scannedTag != null){
                //find all text fields
                table = findViewById(R.id.tableLayout);
                table.setVisibility(View.VISIBLE);
                //// TODO: 05/15/2022 can make init and set in a loop -> arrays.
                idText = findViewById(R.id.idText);
                idHexText = findViewById(R.id.idHexText);
                sizeText = findViewById(R.id.sizeText);
                sectorText = findViewById(R.id.sectorText);
                blockText = findViewById(R.id.blockText);
                techText = findViewById(R.id.techText);
                typeText = findViewById(R.id.typeText);
                //set text
                idText.setText(scannedTag.getId());
                idHexText.setText(scannedTag.getHexID());
                sizeText.setText(scannedTag.getSize());
                sectorText.setText(scannedTag.getSectorCount());
                blockText.setText(scannedTag.getBlockCount());
                //vector

                techText.setText("");
                techText.setLines(scannedTag.getTechs().size());
                for(int i=0; i<scannedTag.getTechs().size(); i++){
                    techText.append(scannedTag.getTechs().elementAt(i));
                    if(i != scannedTag.getTechs().size()-1){
                        techText.append("\n");
                    }

                }





                typeText.setText(scannedTag.getType());

            }




        }
    }
    private void clearScreen(){
        sectors = null;
        wasRead = false;

        try{
            table.setVisibility(View.INVISIBLE);
            sectorArea.removeAllViews();
            blockArea.removeAllViews();

        }
        catch(Exception e){
            Log.e("RESET ERROR", "Cannot clear views");
        }
    }

    public void detectIsoDep(Tag tag){
        IsoDep isoDep = IsoDep.get(tag);
        try{
            isoDep.connect();

        }
        catch(Exception e){

        }
    }
    private void detectTagData(Tag tag) {
//        clearScreen();
//        StringBuilder sb = new StringBuilder();


        //generic data
        //print data to text fields.
        //what data?

        byte[] id = tag.getId();

        scannedTag = new NFCData();
        //sb.append("ID (hex): ").append(toHex(id)).append('\n');

        //ID HEX
        scannedTag.setId(toDec(id));
        scannedTag.setHexID(toHex(id));
        Log.i("HEX ID", scannedTag.getHexID());


        //sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        //genericData.add("ID (reversed hex): " + toReversedHex(id));

        //sb.append("ID (dec): ").append(toDec(id)).append('\n');
        // genericData.add()
        // sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');




        String prefix = "android.nfc.tech.";
//        sb.append("Technologies: ");
        String techs = "";
        for (String tech : tag.getTechList()) {
            //TECH LIST
            scannedTag.addTech((tech.substring(prefix.length())));
        }
        for (String tech : tag.getTechList()) {
            String type = "Unknown";
            if (tech.equals(MifareClassic.class.getName())) {
//                sb.append('\n');
                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }

                    scannedTag.setType(type);
                    scannedTag.setSize(mifareTag.getSize());
                    scannedTag.setSectorCount(mifareTag.getSectorCount());
                    scannedTag.setBlockCount(mifareTag.getBlockCount());

                } catch (Exception e) {
//                    sb.append("Mifare classic error: " + e.getMessage());
//                    genericData.add(e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
//                sb.append("Mifare Ultralight type: ");
                scannedTag.setType(type);
//                sb.append(type);
            }
            if(MifareClassic.get(tag).getSize() == 1024){
                new ReadMifareClassic1KTask(MifareClassic.get(tag)).execute();
            }
            else if(MifareClassic.get(tag).getSize() == 4096){
                new ReadMifareClassic4KTask(MifareClassic.get(tag)).execute();
            }
                //isodep can fuck itself
//            if(type == "Unknown"){
//                //try isodep
//                IsoDep isoDep = IsoDep.get(tag);
//                try{
//                    isoDep.connect();
//                }
//                catch(IOException e){
//
//                }
//
//            }
        }
//        Log.v("test",sb.toString());
//        return sb.toString();


    }
    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private int toDec(byte[] bytes) {
        int result = 0;
        int factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public void bruteForce(MifareClassic tag, int numOfSector){

        scannedTag.setSectors(new Sector[numOfSector]);
        for (int s = 0; s < numOfSector; s++) {
            boolean isOpen = false;
//                            bruteBtn.setText("Sector " + s + "/" + numOfSector);
            //https://stackoverflow.com/questions/24291721/reading-a-text-file-line-by-line-in-android
            BufferedReader reader;
            try {
                final InputStream file = getAssets().open("wordlist.txt");
                reader = new BufferedReader(new InputStreamReader(file));
                String line = reader.readLine();
                byte[] currentKey;

                int lineNum = 0;
                scannedTag.sectors[s] = new Sector(tag.getBlockCountInSector(s));
                while (line != null && !isOpen) {
                    // TODO: 05/16/2022

                    currentKey = hexStringToByteArray(line);

//                                    byte[] decoded = Hex.decodeHex("00A0BF");
                    final int sectorProgress = s;
                    final int num = lineNum;
                    //https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setText("Sector " + (sectorProgress+1) + "/" + numOfSector + " done"+"\n"+num);

                        }
                    });
                    if (tag.authenticateSectorWithKeyA(s, currentKey)) {
//                                        progress.setText("Sector " + s + "/" + numOfSector + " done.");


                        //set specific key for specific sector in data object.
                        Log.i("SUCCESS", s + " OPENED with key " + line);
//                        scannedTag.setKey(line);

                        scannedTag.sectors[s].setAccessKey(line);
                        isOpen = true;

                    }

//                                    currentKey = HexFormat.of().parseHex(s);
//                                    Log.d(s + " KEY = ", line);
                    lineNum ++;
                    line = reader.readLine();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            if(!isOpen){
                //no kewy found.
                scannedTag.sectors[s].setIsReadable(false);
                scannedTag.sectors[s].setAccessKey("FFFFFFFFFFFF");
            }

        }


    }


    //READ SECTORS
    private class ReadMifareClassic1KTask extends AsyncTask<Void, Void, Void> {

        /*
        MIFARE Classic tags are divided into sectors, and each sector is sub-divided into blocks.
        Block size is always 16 bytes (BLOCK_SIZE). Sector size varies.
        MIFARE Classic 1k are 1024 bytes (SIZE_1K), with 16 sectors each of 4 blocks.
        */

        MifareClassic taskTag;
        int numOfBlock;
        final int FIX_SECTOR_COUNT = 16;
        boolean success;
        final int numOfSector = 16;
        final int numOfBlockInSector = 4;

        ReadMifareClassic1KTask(MifareClassic tag){
            taskTag = tag;
            success = false;
        }

        @Override
        protected void onPreExecute() {
//            textViewBlock.setText("Reading Tag, don't remove it!");
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(wasRead == false) {
                sectors = new Sector[numOfSector];
                scannedTag.setSectors(sectors);
                try {
                    taskTag.connect();
                    wasRead = true;
//                MifareClassic.KEY_DEFAULT
                    //attemp brute force? try different keys so all isReadable returned true.
                    //track success rate?
                    //count sectors that were possible to open?
//                    byte custom_key1[] = {
//                            (byte) 0xa0, (byte) 0xa1, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4, (byte) 0xa5
//                    };
//                    byte custom_key2[] = {
//                            (byte) 0xd3, (byte) 0xf7, (byte) 0xd3, (byte) 0xf7, (byte) 0xd3, (byte) 0xf7
//                    };
                    String key;
                    if(toBruteforce) {

                        bruteForce(taskTag, numOfSector);

                    }
                    else{
                        //set default key
                        scannedTag.setSectors(new Sector[numOfSector]);
                        for(int s =0;s<numOfSector;s++) {
//                            scannedTag.sectors[s] = new Sector(taskTag.getBlockCountInSector(s));

                            scannedTag.sectors[s] = new Sector(taskTag.getBlockCountInSector(s));
                            scannedTag.sectors[s].setAccessKey("FFFFFFFFFFFF");
//                            scannedTag.sectors[s].setAccessKey("FFFFFFFFFFFF");
                        }
                    }
//                byteArray = { 9, 2, 14, 10 }
                    for (int s = 0; s < numOfSector; s++) {
                        sectors[s] = new Sector(numOfBlockInSector);


//                    Log.i("Loop", " " + s);
                        if (taskTag.authenticateSectorWithKeyA(s, hexStringToByteArray(scannedTag.sectors[s].getAccessKey()))) {
                            //Log.i("SUCCESS", "Sector read | Correct Key");
                            sectors[s].setIsReadable(true);
                            sectors[s].setAccessKey(scannedTag.sectors[s].getAccessKey());
                            for (int b = 0; b < numOfBlockInSector; b++) {
                                int blockIndex = (s * numOfBlockInSector) + b;
                                //array of bytes
                                sectors[s].addBlock(new Block(taskTag.readBlock(blockIndex), blockIndex));
                                //Log.i("BLOCK REaD", "DATA =  " + taskTag.readBlock(blockIndex).toString());
                            }
                        }
                        else {
                            sectors[s].setIsReadable(false);
                            sectors[s].setAccessKey(null);
                            Log.e("FAIL", "Wrong Key " + s);
                        }
                    }
                    scannedTag.setSectors(sectors);
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    sectors = new Sector[40];
                    wasRead = false;
                } finally {
                    if (taskTag != null) {
                        try {
                            taskTag.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //display block
            sectorArea = findViewById(R.id.sectorScrollArea);
            blockArea = findViewById(R.id.blockArea);
            if(success){
                new PrintSectors(ReadActivity.this, sectorArea, blockArea, scannedTag).printAllSectors();
            }
        }
    }
    private class ReadMifareClassic4KTask extends AsyncTask<Void, Void, Void> {

        /*
        MIFARE Classic tags are divided into sectors, and each sector is sub-divided into blocks.
        Block size is always 16 bytes (BLOCK_SIZE). Sector size varies.
        MIFARE Classic 1k are 1024 bytes (SIZE_1K), with 16 sectors each of 4 blocks.
        */

        MifareClassic taskTag;
        int numOfBlock;
        boolean success;
        int numOfSector = 40;
        int numOfBlockInSector_small = 32;
        int numOfBlockInSector = 4;
        byte[][][] buffer = new byte[numOfBlockInSector_small][numOfBlockInSector][MifareClassic.BLOCK_SIZE];
        byte[][][] buffer4kLargeSecotrs = new byte[8][16][MifareClassic.BLOCK_SIZE];

        ReadMifareClassic4KTask(MifareClassic tag){
            taskTag = tag;
            success = false;
        }

        @Override
        protected void onPreExecute() {
//            textViewBlock.setText("Reading Tag, don't remove it!");
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (wasRead == false) {
                sectors = new Sector[numOfSector];
                scannedTag.setSectors(sectors);
                try {
                    taskTag.connect();
                    wasRead = true;
                    //two times go through standard solution
//                sectors 0-15
                    //+= Sector obj.
                    //can only create fixed number of sectors. (40)





                    //scannedTag.addSectors(new Sector(numOfBlockInSector));
                    if(toBruteforce) {

                        bruteForce(taskTag, numOfSector);

                    }
                    else{
                        //set default key
                        scannedTag.setSectors(new Sector[numOfSector]);
                        for(int s =0;s<numOfSector;s++) {
//                            scannedTag.sectors[s] = new Sector(taskTag.getBlockCountInSector(s));

                            scannedTag.sectors[s] = new Sector(taskTag.getBlockCountInSector(s));
                            scannedTag.sectors[s].setAccessKey("FFFFFFFFFFFF");
//                            scannedTag.sectors[s].setAccessKey("FFFFFFFFFFFF");
                        }
                    }
                    Log.i("BRUTE", "DONE");

                    for (int s = 0; s < numOfBlockInSector_small; s++) {
                        //add new sector with 4 blocks to vector.
                        sectors[s] = new Sector(numOfBlockInSector);

                        if (taskTag.authenticateSectorWithKeyA(s, hexStringToByteArray(scannedTag.sectors[s].getAccessKey()))) {
                            //set sector success to true, AccessKey to key
                            sectors[s].setIsReadable(true);
                            sectors[s].setAccessKey(scannedTag.sectors[s].getAccessKey());

                           // Log.i("SUCCESS", "Sector read 0 - 31 | Correct Key " + s);

                            //read blocks.
                            for (int b = 0; b < numOfBlockInSector; b++) {


                                int blockIndex = (s * numOfBlockInSector) + b;


                                buffer[s][b] = taskTag.readBlock(blockIndex);
                                sectors[s].addBlock(new Block(taskTag.readBlock(blockIndex), blockIndex));
                            }
                        } else {

                            sectors[s].setIsReadable(false);
                            sectors[s].setAccessKey(null);
                           // Log.e("FAILURE", "Wrong key " + s);
                        }


                    }

                    int count = 0;
                    for (int s = 32; s < numOfSector; s++) {
                        sectors[s] = new Sector(numOfBlockInSector);


                        if (taskTag.authenticateSectorWithKeyA(s, hexStringToByteArray(scannedTag.sectors[s].getAccessKey()))) {

                            sectors[s].setIsReadable(true);
                            sectors[s].setAccessKey(scannedTag.sectors[s].getAccessKey());

                            //Log.i("SUCCESS", "Sector read 32 - 40 | Correct Key" + s);
                            for (int b = 0; b < 16; b++) {

                                int blockIndex = ((s + count) * 4) + b;
                                buffer4kLargeSecotrs[s - 32][b] = taskTag.readBlock(blockIndex);
                                sectors[s].addBlock(new Block(taskTag.readBlock(blockIndex), blockIndex));
                               // Log.i("Number of blocks", " " + b);
                            }
                            count += 3;
                        } else {
                            sectors[s].setIsReadable(false);
                            sectors[s].setAccessKey(null);
                           // Log.e("FAILURE", "Wrong key " + s);
                        }

                    }


                    scannedTag.setSectors(sectors);
//                sectors 16-31


                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    //tag was lost
                    sectors = new Sector[40];
                    wasRead = false;
                } finally {
                    if (taskTag != null) {
                        try {
                            taskTag.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
            return null;
        }
//        public byte[] bruteforce(){
//            byte[] key;
//
//            return key;
//        }


        //https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java


        @Override
        protected void onPostExecute(Void aVoid) {
            //display block
            //writing ro var to print out.
            sectorArea = findViewById(R.id.sectorScrollArea);
            blockArea = findViewById(R.id.blockArea);

            //Vector<Button> sectorBtns = new Vector<>();

            if (success){
                new PrintSectors(ReadActivity.this, sectorArea, blockArea, scannedTag).printAllSectors();
            }
        }
    }
}