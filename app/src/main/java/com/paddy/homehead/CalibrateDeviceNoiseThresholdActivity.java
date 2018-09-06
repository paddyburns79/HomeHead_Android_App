package com.paddy.homehead;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;


public class CalibrateDeviceNoiseThresholdActivity extends AppCompatActivity {

    // Strings to accept user input data
    String deviceId, ipAddress, devicePassword;

    // Input values to hold input field data
    EditText devicePasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_device_noise_threshold);
        // set action bar with logo
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.homehead_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // linking input values to each input field
        devicePasswordInput = findViewById(R.id.calibrate_device_device_PW_textbox);

        Button btnStart = findViewById(R.id.button_calibrate_device);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v){
                // check if text has been entered in the password field
                if (devicePasswordInput.length() == 0) {
                    // messsage to highlight empty field
                    Toast.makeText(CalibrateDeviceNoiseThresholdActivity.this, "No password entered!", Toast.LENGTH_LONG).show();
                } else {
                    // retrieval of input field data on button click
                    devicePassword = devicePasswordInput.getText().toString();

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
                                executeSSHCommandCalibrateDevice();
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
     * Method to execute a command to calibrate the device audio parameters (listening)
     */
    public void executeSSHCommandCalibrateDevice() throws Exception{
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
            // SSH Channel
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            // Execute command
            channel.setCommand("cd sopare; python test/test_audio.py");
            // Obtain command line output as String for debugging (via InputStream)
            InputStream errStreamCalibrate = channel.getExtInputStream();
            // connect to channel
            channel.connect();
            // debugging logs TAG strings
            String TAG_errStr = "hh_err_log";
            String TAG_errStr_exception = "hh_err_exce";
            // Reading command line output (from Raspberry Pi)
            BufferedReader readerCalibrate = new BufferedReader(new InputStreamReader(errStreamCalibrate));
            StringBuilder outputAddNoise = new StringBuilder();
            String lineCalibrate;
            while ((lineCalibrate = readerCalibrate.readLine()) != null) {
                // append output to StringBuilder
                outputAddNoise.append(lineCalibrate);
                // output to log (debugging)
                Log.i(TAG_errStr, lineCalibrate);
                // Display message while the device calibration process is carried out
                Snackbar.make(findViewById(android.R.id.content),
                        "Device Calibrating - Please Wait", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
            }
            // InputStream exception try/catch
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                Log.e(TAG_errStr, TAG_errStr_exception);
            }
            if (channel.isClosed()) {
                // Display message to confirm the calibration process has been completed (channel closes on completion)
                Snackbar.make(findViewById(android.R.id.content),
                        "Device Successfully Calibrated.", Snackbar.LENGTH_LONG)
                        .setDuration(20000).setAction("Action", null).show();
                // Disconnect channel
                channel.disconnect();
                // clear input field
                devicePasswordInput.getText().clear();

            }
        }

        catch(JSchException e){
            // Snackbar to indicate connection status (failure) and show the error in the UI
            Snackbar.make(findViewById(android.R.id.content),
                    "Error. Check details entered, your internet connection, or if device has been shut down",
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
