package com.example.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    //layouts
    ConstraintLayout nfcAlertLayout, mainLayout;
    TextView text;



    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.main);
        setContentView(R.layout.activity_main);


    }

    public void onClick(View view) {
        if(view.getId() == R.id.readButton){
            Log.i("Changed","Read Layout");
            Intent intentMain = new Intent(MainActivity.this , ReadActivity.class);
            MainActivity.this.startActivity(intentMain);
        }
        if(view.getId() == R.id.emulButton){
            Log.i("Changed","Emul Layout");
            Intent intentEmul = new Intent(this , EmulateActivity.class);
            startActivity(intentEmul);
            // TODO: 05/14/2022 EMULATE PAGE 
//            Intent intentMain = new Intent(MainActivity.this , ReadActivity.class);
//            MainActivity.this.startActivity(intentMain);
        }
    }
//    public void OnClick(View v){
//        if(v == readButton){
//            Intent intentMain = new Intent(MainActivity.this , ReadActivity.class);
//            MainActivity.this.startActivity(intentMain);
//        }
//    }

    public void onPause() {
        super.onPause();

    }

    public void onResume() {
        super.onResume();

    }


}