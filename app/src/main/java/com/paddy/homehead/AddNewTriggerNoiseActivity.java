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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

public class AddNewTriggerNoiseActivity extends AppCompatActivity {

    // String to accept ssh cmd line output
    String cmdOutput;

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

       final String TAG = "TESTING";

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
                            Log.i(TAG,executeSSHCommandAddNoise()+cmdOutput);
                           // executeSSHCommandAddNoise();
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
     */
    /*public void executeSSHCommandAddNoise(){
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
            channel.setCommand("cd sopare; ./sopare.py -v -t "+noiseDescription);
            channel.connect();
            // output if channel successfully opened
            while (!channel.isClosed()) {
                // Snackbar to prompt user to play noise / speak
                Snackbar.make(findViewById(android.R.id.content),
                        "Recording : Play Noise / Speak Phrase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            // Snackbar to confirm recording has stopped
            Snackbar.make(findViewById(android.R.id.content),
                    "Recording Completed", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        catch(JSchException e){
            // Snackbar to indicate connection status (failure) and show the error in the UI
            Snackbar.make(findViewById(android.R.id.content),
                    "Error. Check details entered, your internet connection, or if device has been shut down",
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();
        }
    }*/

    /**
     * Method to execute a command to save new noise to device dictionary via SSH connection
     */
    public void executeSSHCommandSaveNoiseToDictionary(){
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
            channel.setCommand("cd sopare; ./sopare.py -c");
            channel.connect();
            // output if channel successfully opened
            if (channel.isConnected()) {
                // Snackbar to indicate connection status : success
                Snackbar.make(findViewById(android.R.id.content),
                        "Noise Successfully Saved to Dictionary", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // clear input fields
                devicePasswordInput.getText().clear();
                deviceIdInput.getText().clear();
                noiseDescriptionInput.getText().clear();
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



    public String executeSSHCommandAddNoise() throws Exception {
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
        ChannelExec channel = (ChannelExec)
                session.openChannel("exec");
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //channel.setOutputStream(baos);
        //cmdOutput = new String(baos.toString());

        /*OutputStream ops = channel.getOutputStream();
        PrintStream ps = new PrintStream(ops);
        channel.connect();
        ps.println("cd sopare; ./sopare.py -v -t "+noiseDescription);*/

        // Execute command
        channel.setCommand("cd sopare; ./sopare.py -v -t "+noiseDescription);
        channel.setInputStream(null);
        channel.connect();

        InputStream in = channel.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String jarOutput = reader.readLine();

       /* while ((jarOutput = reader.readLine().toLowerCase()) != null)
            System.out.println(jarOutput);
        reader.close();*/

        // Snackbar to indicate connection status : success
        Snackbar.make(findViewById(android.R.id.content),
                "Recording : Play Noise / Speak Phrase ", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        return jarOutput;

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
