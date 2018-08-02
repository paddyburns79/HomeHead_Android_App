package com.paddy.homehead;

import android.content.DialogInterface;
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

public class SystemShutdownActivity extends AppCompatActivity {

    // Strings to accept user input data
    String deviceId, ipAddress, devicePassword;

    // Input values to hold input field data
    EditText deviceIdInput;
    EditText ipAddressInput;
    EditText devicePasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_shutdown);

        // linking input values to each input field
        deviceIdInput = (EditText) findViewById(R.id.start_device_deviceID_textbox);
        ipAddressInput = (EditText) findViewById(R.id.start_device_IPAdd_textbox);
        devicePasswordInput = (EditText) findViewById(R.id.start_device_Device_PW_textbox);

        Button btnShutdown = findViewById(R.id.button_shutdown_submit);
        btnShutdown.setOnClickListener(new View.OnClickListener() {
            //start execution of ssh commands
            @Override
            public void onClick(View v){
                // retrieval of input field data on button click
                deviceId = deviceIdInput.getText().toString();
                ipAddress = ipAddressInput.getText().toString();
                devicePassword = devicePasswordInput.getText().toString();

                // call alert dialog method
                shutdownBtnAlertDialog();
            }
        });

    }

    /**
     * Method to execute a command to shutdown the device via SSH connection
     */
    public void executeSSHcommandShutdown(){
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
            channel.setCommand("sudo shutdown -h now");
            channel.connect();
            channel.disconnect();
            // Snackbar to indicate connection status : success
            Snackbar.make(findViewById(android.R.id.content),
                    "Device successfully shut down. You can now safely turn device off at mains supply", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        catch(JSchException e){
            // Snackbar to indicate connection status (failure) and show the error in the UI
            Snackbar.make(findViewById(android.R.id.content),
                    "Error. Please check details entered or your internet service",
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();
        }
    }

    /**
     * Method to call a custom Alert Dialog box
     */
    public void shutdownBtnAlertDialog() {
    // set Alert Dialog box to confirm system shutdown
    AlertDialog.Builder shutdownConfirm = new AlertDialog.Builder(SystemShutdownActivity.this);
    // allow alert dialog to be cancelled by clicking area outside of the box
    shutdownConfirm.setCancelable(true);
    // set messages
    shutdownConfirm.setTitle("Confirm Device Shutdown");
    shutdownConfirm.setMessage("Are you sure you wish to shutdown the HomeHead Raspberry Pi?");
    // set negative 'cancel' button
    shutdownConfirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int whichButton) {
            dialogInterface.cancel();
        }
    });

    // set positive 'yes' button
    shutdownConfirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
           new AsyncTask<Integer, Void, Void>(){
                @Override
                protected Void doInBackground(Integer... params) {
                    try {
                        // call method to execute SSH command
                        executeSSHcommandShutdown();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute(1);
        }
    });
    shutdownConfirm.show();
    }
}