package com.paddy.homehead;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.util.IOUtils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Properties;

public class CalibrateDeviceNoiseThresholdActivity extends AppCompatActivity {

    // Strings to accept user input data
    String deviceId, ipAddress, devicePassword;
    int configCycles = 1;

    // Input values to hold input field data
    EditText deviceIdInput;
    EditText devicePasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_device_noise_threshold);

        // linking input values to each input field
        deviceIdInput = (EditText) findViewById(R.id.calibrate_device_deviceID_textbox);
        devicePasswordInput = (EditText) findViewById(R.id.calibrate_device_device_PW_textbox);

        Button btnStart = findViewById(R.id.button_calibrate_device);
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

                final String TAG = "TESTING";

                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            executeSSHCommandCalibrateDevice();
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
     * Method to execute a command to calibrate the device audio parameters (listening)
     */
    public void executeSSHCommandCalibrateDevice(){
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
            channel.setCommand("cd sopare; python test/test_audio.py");
            channel.connect();
            //channel.disconnect();

            // Snackbar output display to indicate calibration status
            while ((configCycles ==1) && (channel.isClosed()==false)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Processing Request"+channel.getExitStatus()+channel.isClosed(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
           }
           // failsafe to ensure config cycle only runs once
            configCycles++;
            Snackbar.make(findViewById(android.R.id.content),
                    "Device is calibrated"+channel.getExitStatus()+channel.isClosed(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        catch(JSchException e){
            // Snackbar to indicate connection status (failure) and show the error in the UI
            Snackbar.make(findViewById(android.R.id.content),
                    "Error. Please check details entered (incl. Rasperry Pi IP Address stored) or your internet connection",
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();
        }
    }



/*



    public String executeSSHCommandCalibrateDevice() throws Exception {
        String user = deviceId;
        String password = devicePassword;
        String host = ipAddress;
        int port=22;

        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
                session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channelssh.setOutputStream(baos);

        // Execute command
        channelssh.setCommand("cd sopare; python test/test_audio.py");
        channelssh.connect();
        //channelssh.disconnect();

        // Snackbar to indicate connection status : success
        */
/*Snackbar.make(findViewById(android.R.id.content),
                "Device is calibrated : " +baos.toString(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*//*


        return baos.toString();

    }
*/

}
