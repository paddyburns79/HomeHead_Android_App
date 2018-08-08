package com.paddy.homehead;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class StartDeviceListeningActivity extends AppCompatActivity {

    // Strings to accept user input data
    String deviceId, ipAddress, devicePassword;

    // Input values to hold input field data
    EditText deviceIdInput;
    EditText devicePasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_device_listening);

        // linking input values to each input field
        deviceIdInput = (EditText) findViewById(R.id.start_device_deviceID_textbox);
        devicePasswordInput = (EditText) findViewById(R.id.start_device_Device_PW_textbox);

        Button btnStart = findViewById(R.id.button_start_listen);
        btnStart.setOnClickListener(new View.OnClickListener() {
            //start execution of ssh commands
            @Override
            public void onClick(View v){
                // retrieval of input field data on button click
                deviceId = deviceIdInput.getText().toString();
                devicePassword = devicePasswordInput.getText().toString();

                // Accessing SharedPreferences Data (Stored Device RBP IP Address)
                SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
                ipAddress = ipAddressSharedPref.getString("rbp_ip_address", "");


                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            executeSSHCommandStartListening();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(1);
            }
        });
    }

    /**
     * Method to execute a command to commence listening via SSH connection
     */
    public void executeSSHCommandStartListening(){
        String user = deviceId;
        String password = devicePassword;
        String host = ipAddress;
        int port=22;
        try{

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(10000);
            session.connect();
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand("cd sopare; ./sopare.py -l");
            channel.connect();
            // output if channel successfully opened
            if (channel.isConnected()) {
                // Snackbar to indicate connection status : success
                Snackbar.make(findViewById(android.R.id.content),
                        "Device is Actively Listening", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // clear input fields
                deviceIdInput.getText().clear();
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
