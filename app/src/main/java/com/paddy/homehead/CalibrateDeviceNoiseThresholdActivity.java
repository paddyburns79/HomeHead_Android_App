package com.paddy.homehead;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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

            // Display message while the device calibration process is carried out (channel remains open during process)
            while ((configCycles ==1) && (channel.isClosed()==false)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Device Calibrating", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
           }
           // failsafe to ensure config cycle only runs once
            configCycles++;
            // Display message to confirm the calibration process has been completed (channel closes on completion)
            Snackbar.make(findViewById(android.R.id.content),
                    "Device Successfully Calibrated.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        catch(JSchException e){
            // Snackbar to indicate connection status (failure) and show the error in the UI
            Snackbar.make(findViewById(android.R.id.content),
                    "Error. Check details entered (incl. Raspberry Pi IP Address stored) or your internet connection",
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();
        }
        // Resetting the configCycle check in the event the user wishes to recalibrate the device
        configCycles = 1;
    }

    /**
     * Method to display tooltip (Info on calibrating device) on image button click
     * @param view
     */
    public void displayCalibrateDeviceTooltip(View view) {

        // set Alert Dialog box to confirm system shutdown
        AlertDialog.Builder shutdownConfirm = new AlertDialog.Builder(CalibrateDeviceNoiseThresholdActivity.this);
        // allow alert dialog to be cancelled by clicking area outside of the box
        shutdownConfirm.setCancelable(true);
        // set messages
        shutdownConfirm.setTitle("Calibrating Device");
        shutdownConfirm.setMessage("Calibration of the device noise detection parameters takes approximately 90 seconds, after which the message 'Device Successfully Calibrated' will be displayed. Please ensure no unusual loud noises occur during this process.\n\nRepeat calibration if you feel noise events are not being registered.");
        // set negative 'cancel' button
        shutdownConfirm.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                dialogInterface.cancel();
            }
        });

        shutdownConfirm.show();

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
