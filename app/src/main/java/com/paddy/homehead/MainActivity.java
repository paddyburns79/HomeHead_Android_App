package com.paddy.homehead;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity  {

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set onclick listener for Enable Listening Button (nav to activity)
        Button btnStart = findViewById(R.id.button_start_listen_activity);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // link to intent
                Intent startListenIntent = new Intent(MainActivity.this, StartDeviceListeningActivity.class);
                startActivity(startListenIntent);
            }
        });

        // set onclick listener for Disable Listening Button (nav to activity)
        Button btnStop = findViewById(R.id.button_stop_listen_activity);
        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // link to intent
                Intent stopListenIntent = new Intent(MainActivity.this, StopDeviceListeningActivity.class);
                startActivity(stopListenIntent);
            }
        });

        // set onclick listener for System Shutdown Image Button (nav to activity)
        ImageButton buttonShutdown = findViewById(R.id.buttonDeviceShutdown);
        buttonShutdown.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  // link to intent
                  Intent systemShutdownIntent = new Intent(MainActivity.this, SystemShutdownActivity.class);
                  startActivity(systemShutdownIntent);

              }
          });
    }


    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

}

