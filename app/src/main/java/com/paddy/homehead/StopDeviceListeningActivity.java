package com.paddy.homehead;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Objects;

public class StopDeviceListeningActivity extends AppCompatActivity {

    // Strings to accept user input data
    String deviceId, ipAddress, devicePassword;

    // Input values for each inout field
    EditText devicePasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_device_listening);
        // add logo to action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.homehead_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // linking input values to each input field
        devicePasswordInput = (EditText) findViewById(R.id.start_device_Device_PW_textbox);

        Button btnStop = findViewById(R.id.button_stop_listen);
        btnStop.setOnClickListener(new View.OnClickListener() {
            //start execution of ssh commands
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                // check if text has been entered in the password field
                if (devicePasswordInput.length() == 0) {
                    // messsage to highlight empty fields
                    Toast.makeText(StopDeviceListeningActivity.this, "No password entered!", Toast.LENGTH_LONG).show();
                } else {
                    // retrieval of input field data on button click
                    devicePassword = devicePasswordInput.getText().toString().trim();

                    // Accessing SharedPreferences Data (Stored Device RBP IP Address)
                    SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
                    ipAddress = ipAddressSharedPref.getString("rbp_ip_address", "");

                    // Accessing SharedPreferences Data (Stored Device ID)
                    SharedPreferences deviceIDSharedPref = getSharedPreferences("device_id_shared_pref", Context.MODE_PRIVATE);
                    deviceId = deviceIDSharedPref.getString("rbp_device_id", "");

                    // asynchronous calling of method to execute SSH connection
                    new AsyncTask<Integer, Void, Void>() {
                        @Override
                        protected Void doInBackground(Integer... params) {
                            try {
                                executeSSHcommandStopListening();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute(1);
                }
            }
        });
    }

    /**
     * Method to execute a command to disable device listening via SSH connection
     */
    public void executeSSHcommandStopListening(){
        int port=22;
        try{
            // Set SSH Session and Parameters
            JSch jsch = new JSch();
            Session session = jsch.getSession(deviceId, ipAddress, port);
            session.setPassword(devicePassword);
            // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(10000);
            // connect session
            session.connect();
            // SSH Channel
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            // Execute command
            channel.setCommand("killall python");
            // connect SSH channel
            channel.connect();
            // discnnect channel after execution of command
            channel.disconnect();
            // Snackbar to indicate connection status : success
            if (channel.isClosed()) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Listening Mode Successfully Disabled! ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // clear input fields
                devicePasswordInput.getText().clear();
                }
        }
        catch(JSchException e){
            // Snackbar to indicate connection status (failure) and show the error in the UI
            Snackbar.make(findViewById(android.R.id.content),
                    "Error. Check details entered, your internet connection, or if device has been shut down",
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();
        }
    }
}