package com.paddy.homehead;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DeviceConfigMainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config_main_menu);


        // set onclick listener for Add/Edit IP Address Button (nav to activity)
        Button btnAddEditIPAdd = findViewById(R.id.button_add_edit_IPAddress);
        btnAddEditIPAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addEditIPAddIntent = new Intent(DeviceConfigMainMenuActivity.this, AddEditIPAddressActivity.class);
                startActivity(addEditIPAddIntent);
            }
        });

        // set onclick listener for Add New Trigger Noise (nav to activity)
        Button btnAddNewTriggerNoise = findViewById(R.id.button_add_new_trigger_noise);
        btnAddNewTriggerNoise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewTriggerNoiseIntent = new Intent(DeviceConfigMainMenuActivity.this, AddNewTriggerNoiseActivity.class);
                startActivity(addNewTriggerNoiseIntent);
            }
        });

        // set onclick listener for Calibrate Noise Trigger Levels Button (nav to activity)
        Button btnCalibrateNoiseLevels = findViewById(R.id.button_calibrate_bg_noise);
        btnCalibrateNoiseLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent calibrateNoiseIntent = new Intent(DeviceConfigMainMenuActivity.this, CalibrateDeviceNoiseThresholdActivity.class);
                startActivity(calibrateNoiseIntent);
            }
        });

        // set onclick listener for Delete Saved Trigger Noises Button (nav to activity)
        Button btnDeleteSavedNoises = findViewById(R.id.button_delete_trigger_noise);
        btnDeleteSavedNoises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent deleteSavedNoisesIntent = new Intent(DeviceConfigMainMenuActivity.this, DeleteTriggerNoiseActivity.class);
                startActivity(deleteSavedNoisesIntent);
            }
        });
    }
}
