package com.paddy.homehead;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class MainActivity extends AppCompatActivity  {

    // Strings to accept user input data
    String deviceId, ipAddress, devicePassword;

    // Input values for each input field
    EditText deviceIdInput;
    EditText ipAddressInput;
    EditText devicePasswordInput;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set onclick listener for Enable Listening Button
        Button btnStart = findViewById(R.id.button_start_listen_activity);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent startListenIntent = new Intent(MainActivity.this, StartDeviceListeningActivity.class);
                startActivity(startListenIntent);
            }
        });

        // set onclick listener for Disable Listening Button
        Button btnStop = findViewById(R.id.button_stop_listen_activity);
        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent stopListenIntent = new Intent(MainActivity.this, StopDeviceListeningActivity.class);
                startActivity(stopListenIntent);
            }
        });

        ImageButton buttonShutdown = findViewById(R.id.buttonDeviceShutdown);
        buttonShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder shutdownBuilder = new AlertDialog.Builder(MainActivity.this);
                View shutdownView = getLayoutInflater().inflate(R.layout.dialog_device_shutdown, null);

                deviceIdInput = (EditText) shutdownView.findViewById(R.id.start_device_deviceID_textbox);
                ipAddressInput = (EditText) shutdownView.findViewById(R.id.start_device_IPAdd_textbox);
                devicePasswordInput = (EditText) shutdownView.findViewById(R.id.start_device_Device_PW_textbox);
                Button shutdownConfirmButtonInput = (Button) shutdownView.findViewById(R.id.button_shutdown_confirm);

                // activate submit button function and check that fields are not empty
                shutdownConfirmButtonInput.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!deviceIdInput.getText().toString().isEmpty() && !ipAddressInput.getText().toString().isEmpty() && !devicePasswordInput.getText().toString().isEmpty()) {
                            shutdownSystem();
                            // retrieval of input field data on button click
                            deviceId = deviceIdInput.getText().toString();
                            ipAddress = ipAddressInput.getText().toString();
                            devicePassword = devicePasswordInput.getText().toString();

                            new AsyncTask<Integer, Void, Void>(){
                                @Override
                                protected Void doInBackground(Integer... params) {
                                    try {
                                        shutdownSystem();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            }.execute(1);

                        } else {
                            Toast.makeText(MainActivity.this,
                                    R.string.error_login_msg,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // activate cancel button function
                shutdownBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                // allows the custom Alert Dialog to be shown
                shutdownBuilder.setView(shutdownView);
                AlertDialog shutdownDialog = shutdownBuilder.create();
                shutdownDialog.show();

            }
        });

    }

    /*public void shutdownBtnAlertDialog() {
        // set Alert Dialog box to confirm system shutdown
        AlertDialog.Builder shutdownConfirm = new AlertDialog.Builder(MainActivity.this);
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

            }
        });
        shutdownConfirm.show();
    }*/

    public void shutdownSystem() {
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
            channel.setCommand("killall python");
            channel.connect();
            channel.disconnect();
            // Snackbar to indicate connection status : success
            Snackbar.make(findViewById(android.R.id.content),
                    "Successfully Stopped!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
        catch(JSchException e){
            // Snackbar to indicate connection status (failure) and show the error in the UI
            Snackbar.make(findViewById(android.R.id.content),
                    "Connection Error : "+e.getMessage(),
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();
        }

    }


    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

}

