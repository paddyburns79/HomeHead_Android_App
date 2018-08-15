package com.paddy.homehead;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DeleteTriggerNoiseActivity extends AppCompatActivity {

    // Strings to accept user input data
    String deviceId, ipAddress, devicePassword, noiseToDelete;

    // Input values to hold input field data
    EditText deviceIdInput;
    EditText devicePasswordInput;
    EditText noiseToDeleteInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_trigger_noise);

        // linking input values to each input field
        deviceIdInput = (EditText) findViewById(R.id.start_device_deviceID_textbox);
        devicePasswordInput = (EditText) findViewById(R.id.start_device_Device_PW_textbox);
        noiseToDeleteInput = (EditText) findViewById(R.id.noise_to_delete_textbox);

        // onClick listener for Delete Specific Noise button press
        Button btnDeleteSpecificNoise = findViewById(R.id.delete_specific_noise_button);
        btnDeleteSpecificNoise.setOnClickListener(new View.OnClickListener() {
            //start execution of ssh commands
            @Override
            public void onClick(View v){
                // retrieval of input field data on button click
                deviceId = deviceIdInput.getText().toString();
                devicePassword = devicePasswordInput.getText().toString();
                noiseToDelete = noiseToDeleteInput.getText().toString();

                // Accessing SharedPreferences Data (Stored Device RBP IP Address)
                SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
                ipAddress = ipAddressSharedPref.getString("rbp_ip_address", "");

                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            executeSSHCommandDeleteSpecificNoise();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(1);
            }
        });

        // onClick listener for Delete ALL Noises button press
        Button btnDeleteAllNoises = findViewById(R.id.delete_all_noises_button);
        btnDeleteAllNoises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // retrieval of input field data on button click
                deviceId = deviceIdInput.getText().toString();
                devicePassword = devicePasswordInput.getText().toString();

                // Accessing SharedPreferences Data (Stored Device RBP IP Address)
                SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
                ipAddress = ipAddressSharedPref.getString("rbp_ip_address", "");

                // call method to diplay alert dialog box to confirm delete all noises stored
                confirmDeleteAllBtnAlertDialog();
            }
        });
    }

    /**
     * Method to execute a command to delete a specific trigger noise saved on the RBP Device
     */
    public void executeSSHCommandDeleteSpecificNoise(){
        int port=22;
        try{

            JSch jsch = new JSch();
            Session session = jsch.getSession(deviceId, ipAddress, port);
            session.setPassword(devicePassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(10000);
            session.connect();
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand("cd sopare; ./sopare.py -d "+noiseToDelete);
            channel.connect();

            // Disconnect channel
            channel.disconnect();
            if(!channel.isConnected()) {
                // Display message to confirm noise has been deleted
                Snackbar.make(findViewById(android.R.id.content),
                        "Trigger Noise Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // clear input fields
                deviceIdInput.getText().clear();
                devicePasswordInput.getText().clear();
                noiseToDeleteInput.getText().clear();
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
     * Method to call a custom Alert Dialog box to confirm delete all noise files
     */
    public void confirmDeleteAllBtnAlertDialog() {
        // set Alert Dialog box to confirm system shutdown
        AlertDialog.Builder deleteAllConfirm = new AlertDialog.Builder(DeleteTriggerNoiseActivity.this);
        // allow alert dialog to be cancelled by clicking area outside of the box
        deleteAllConfirm.setCancelable(true);
        // set messages
        deleteAllConfirm.setTitle("Confirm Delete All Noises");
        deleteAllConfirm.setMessage("Are you sure you wish to delete all trigger noises stored?");
        // set negative 'cancel' button
        deleteAllConfirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                dialogInterface.cancel();
            }
        });

        // set positive 'yes' button
        deleteAllConfirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            // call method to execute SSH command
                            executeSSHCommandDeleteAllNoisesRawFiles();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(1);
            }
        });
        deleteAllConfirm.show();
    }


    /**
     * Method to execute a command to delete .raw files for ALL trigger noises saved on the RBP Device
     */
    public void executeSSHCommandDeleteAllNoisesRawFiles(){
        int port=22;
        try{

            JSch jsch = new JSch();
            Session session = jsch.getSession(deviceId, ipAddress, port);
            session.setPassword(devicePassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(10000);
            session.connect();
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand("cd sopare; rm dict/*.raw");
            channel.connect();

            // Functions conditional on the channel being open (i.e. command being executed)
            while (!channel.isClosed()) {
                // Display message to confirm noise has been deleted
                Snackbar.make(findViewById(android.R.id.content),
                        "Deleting Noises", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            // Call method to delete all saved noise references (for complete deletion)
            executeSSHCommandDeleteAllNoiseEntries();
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
     * Method to execute a command to delete file references for ALL trigger noises saved on the RBP Device
     */
    public void executeSSHCommandDeleteAllNoiseEntries(){
        int port=22;
        try{

            JSch jsch = new JSch();
            Session session = jsch.getSession(deviceId, ipAddress, port);
            session.setPassword(devicePassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(10000);
            session.connect();
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand("cd sopare; ./sopare.py -d '*'");
            channel.connect();

            // Functions conditional on the channel being open (i.e. command being executed)
            while (!channel.isClosed()) {
                // Display message to confirm noise has been deleted
                Snackbar.make(findViewById(android.R.id.content),
                        "Deleting Noises", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            // Display message to confirm noise has been deleted
            Snackbar.make(findViewById(android.R.id.content),
                    "All Trigger Noises Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            // Disconnect channel
            channel.disconnect();

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
