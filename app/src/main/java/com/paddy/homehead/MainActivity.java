package com.paddy.homehead;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity  {

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.button_start_listen_activity);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent startListenIntent = new Intent(MainActivity.this, StartDeviceListeningActivity.class);
                startActivity(startListenIntent);
            }
        });

        Button btnStop = findViewById(R.id.button_stop_listen_activity);
        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent stopListenIntent = new Intent(MainActivity.this, StopDeviceListeningActivity.class);
                startActivity(stopListenIntent);
            }
        });

    }


    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

}

