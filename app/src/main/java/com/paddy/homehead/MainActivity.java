package com.paddy.homehead;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity  {

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //

        // set onclick listener for Enable Listening Button (nav to activity)
        ImageButton btnStart = findViewById(R.id.mainmenu_start_listening_imgbtn);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent startListenIntent = new Intent(MainActivity.this, StartDeviceListeningActivity.class);
                startActivity(startListenIntent);
            }
        });

        // set onclick listener for Disable Listening Button (nav to activity)
        ImageButton btnStop = findViewById(R.id.mainmenu_disable_listening_imgbtn);
        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent stopListenIntent = new Intent(MainActivity.this, StopDeviceListeningActivity.class);
                startActivity(stopListenIntent);
            }
        });

        // set onclick listener for System Config Menu Image Button (nav to activity)
        ImageButton buttonSystemConfig = findViewById(R.id.buttomDeviceConfig);
        buttonSystemConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent systemConfigMenuIntent = new Intent(MainActivity.this, DeviceConfigMainMenuActivity.class);
                startActivity(systemConfigMenuIntent);
            }
        });

        // set onclick listener for Notifications Received Image Button (nav to activity)
        ImageButton buttonMsgsReceived = findViewById(R.id.view_notifications_imgbtn);
        buttonMsgsReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewNotificationsReceivedIntent = new Intent(MainActivity.this, ViewNotificationsReceivedActivity.class);
                startActivity(viewNotificationsReceivedIntent);
            }
        });

        // set onclick listener for System Shutdown Image Button (nav to activity)
        ImageButton buttonShutdown = findViewById(R.id.buttonDeviceShutdown);
        buttonShutdown.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
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

