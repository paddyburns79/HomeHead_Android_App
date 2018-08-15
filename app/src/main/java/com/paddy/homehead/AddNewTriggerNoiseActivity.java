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
import android.widget.EditText;
import android.widget.ImageButton;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class AddNewTriggerNoiseActivity extends AppCompatActivity {

    // Strings to accept user input data
    String noiseDescription, deviceId, ipAddress, devicePassword;

    // Input values to hold input field data
    EditText deviceIdInput;
    EditText devicePasswordInput;
    EditText noiseDescriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trigger_noise);

       //final String TAG = "TESTING";

        // linking input values to each input field
        deviceIdInput = (EditText) findViewById(R.id.calibrate_device_deviceID_textbox);
        devicePasswordInput = (EditText) findViewById(R.id.calibrate_device_device_PW_textbox);
        noiseDescriptionInput = (EditText) findViewById(R.id.add_noise_name_textbox);

        ImageButton btnStartNewNoiseRecording = findViewById(R.id.add_noise_speak_imgBtn);
        btnStartNewNoiseRecording.setOnClickListener(new View.OnClickListener() {
            //start execution of ssh commands
            @Override
            public void onClick(View v){
                // retrieval of input field data on button click
                deviceId = deviceIdInput.getText().toString();
                devicePassword = devicePasswordInput.getText().toString();
                noiseDescription = noiseDescriptionInput.getText().toString();

                // Accessing SharedPreferences Data (Stored Device RBP IP Address)
                SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
                ipAddress = ipAddressSharedPref.getString("rbp_ip_address", "");

                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            executeSSHCommandAddNoise();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(1);
            }
        });


        ImageButton btnSaveToDictionary = findViewById(R.id.add_noise_dictionary_imgBtn);
        btnSaveToDictionary.setOnClickListener(new View.OnClickListener() {
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
                            executeSSHCommandSaveNoiseToDictionary();
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
     * Method to execute a command to train a new noise via SSH connection
     * @throws Exception
     */
    public void executeSSHCommandAddNoise() throws Exception {
        int port=22;

        try {
            // Set SSH Session and Parameters
            JSch jsch = new JSch();
            Session session = jsch.getSession(deviceId, ipAddress, port);
            session.setPassword(devicePassword);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            session.connect();

            // SSH Channel
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            // Execute command
            channel.setCommand("cd sopare; ./sopare.py -v -t " + noiseDescription);
            // Obtain command line output as String (via InputStream)
            StringBuilder errorBuffer = new StringBuilder();
            InputStream errStream = channel.getExtInputStream();
            // connect to channel
            channel.connect();

            // debugging logs TAG strings
            String TAG_errStr = "hh_err_log";
            String TAG_errStr_exception = "hh_err_exce";
            // command line output comparison String (to trigget notification)
            String cmdRecordMsg = "INFO:sopare.recorder:start endless recording";

            // Reading command line output (from Raspberry Pi)
            BufferedReader reader = new BufferedReader(new InputStreamReader(errStream));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // append output to StringBuilder
                out.append(line);
                // output to log (debugging)
                Log.i(TAG_errStr, line);
                // comparison statement to output message to start recording
                if(line.equals(cmdRecordMsg)) {
                    // Snackbar to prompt user
                    Snackbar.make(findViewById(android.R.id.content),
                            "Record Noise / Phrase", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            // InputStream exception try/catch
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                Log.e(TAG_errStr, TAG_errStr_exception);
            }

            // Snackbar to indicate process has completed
            Snackbar.make(findViewById(android.R.id.content),
                    "Recording Completed", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            // Disconnect channel
            channel.disconnect();

        // SSH channel catch
        } catch(JSchException e){
            // Snackbar to indicate connection status (failure) and show the error in the UI
            Snackbar.make(findViewById(android.R.id.content),
                    "Error. Check details entered, your internet connection, or if device has been shut down",
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();
        }
    }

    /**
     * Method to execute a command to save new noise to device dictionary via SSH connection
     */
    public void executeSSHCommandSaveNoiseToDictionary(){
        int port=22;
        try{
            // Set SSH Session and Parameters
            JSch jsch = new JSch();
            Session session = jsch.getSession(deviceId, ipAddress, port);
            session.setPassword(devicePassword);
            // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(10000);
            // Connect Session
            session.connect();
            //  Create SSH channel
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            // Execute command
            channel.setCommand("cd sopare; ./sopare.py -c");
            channel.connect();
            // output if channel successfully opened
            if (channel.isConnected()) {
                // Snackbar to indicate connection status : success
                Snackbar.make(findViewById(android.R.id.content),
                        "Noise Successfully Saved to Dictionary", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Disconnect channel
                channel.disconnect();
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

    /**
     * Method to display tooltip (Info on adding a new trigger noise) on image button click
     * @param view
     */
    public void displayNewNoiseTooltip(View view) {

        // set Alert Dialog box to confirm system shutdown
        AlertDialog.Builder shutdownConfirm = new AlertDialog.Builder(AddNewTriggerNoiseActivity.this);
        // allow alert dialog to be cancelled by clicking area outside of the box
        shutdownConfirm.setCancelable(true);
        // set messages
        shutdownConfirm.setTitle("Adding New Trigger Noise");
        shutdownConfirm.setMessage("To add a new trigger noise (i.e. speech or a consistent, repeatable sound) enter the required details, press record and follow the on-screen prompt.\n\nEach noise should be recorded at least 3 times before saving to the device dictionary.\n\nIf recording speech, record one word only.");
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
