package com.paddy.homehead;

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

public class StopDeviceListeningActivity extends AppCompatActivity {

    // Strings to accept user input data
    String deviceId, ipAddress, devicePassword;

    // Input values for each inout field
    EditText deviceIdInput;
    EditText ipAddressInput;
    EditText devicePasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_device_listening);

        // linking input values to each input field
        deviceIdInput = (EditText) findViewById(R.id.start_device_deviceID_textbox);
        ipAddressInput = (EditText) findViewById(R.id.start_device_IPAdd_textbox);
        devicePasswordInput = (EditText) findViewById(R.id.start_device_Device_PW_textbox);

        Button btnStop = findViewById(R.id.button_stop_listen);
        btnStop.setOnClickListener(new View.OnClickListener() {
            //start execution of ssh commands
            @Override
            public void onClick(View v){
                // retrieval of input field data on button click
                deviceId = deviceIdInput.getText().toString();
                ipAddress = ipAddressInput.getText().toString();
                devicePassword = devicePasswordInput.getText().toString();

                new AsyncTask<Integer, Void, Void>(){
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
        });
    }

    /**
     * Method to execute a command to disable device listening via SSH connection
     */
    public void executeSSHcommandStopListening(){
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
                    "Listening Mode Successfully Disabled!", Snackbar.LENGTH_LONG)
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
}