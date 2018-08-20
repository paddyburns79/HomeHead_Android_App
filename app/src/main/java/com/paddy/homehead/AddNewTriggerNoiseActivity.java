package com.paddy.homehead;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class AddNewTriggerNoiseActivity extends AppCompatActivity {

    // Access a Cloud Firestore instance
    FirebaseFirestore noisesAddedDB = FirebaseFirestore.getInstance();

    // Strings to accept user input data
    String noiseDescription,ipAddress, deviceId, devicePassword;

    // Input values to hold input field data
    EditText devicePasswordInput;
    EditText noiseDescriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trigger_noise);
        // add logo to action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.homehead_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Cloud Firestore instance settings
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        noisesAddedDB.setFirestoreSettings(settings);

       /* // set onclick listener for View Saved Noises (nav to activity)
        Button btnViewSavedNoises = findViewById(R.id.view_saved_noises_button);
        btnViewSavedNoises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewSavedNoisesIntent = new Intent(AddNewTriggerNoiseActivity.this, ViewSavedNoisesActivity.class);
                startActivity(viewSavedNoisesIntent);
            }
        });*/

        // linking input values to each input field
        devicePasswordInput = (EditText) findViewById(R.id.calibrate_device_device_PW_textbox);
        noiseDescriptionInput = (EditText) findViewById(R.id.add_noise_name_textbox);

        ImageButton btnStartNewNoiseRecording = findViewById(R.id.add_noise_speak_imgBtn);
        btnStartNewNoiseRecording.setOnClickListener(new View.OnClickListener() {
            //start execution of ssh commands
            @Override
            public void onClick(View v){
                // check if both fields are empty
                if ((devicePasswordInput.length() == 0) && (noiseDescriptionInput.length()== 0)) {
                    // messsage to highlight empty fields
                    Toast.makeText(AddNewTriggerNoiseActivity.this, "No password or noise description entered!", Toast.LENGTH_LONG).show();
                // check if text has been entered in the password field
                } else if (devicePasswordInput.length() == 0) {
                    // messsage to highlight empty password field
                    Toast.makeText(AddNewTriggerNoiseActivity.this, "No password entered!", Toast.LENGTH_LONG).show();
                // check if text has been entered in the 'noise description' field
                } else if (noiseDescriptionInput.length()== 0) {
                    // messsage to highlight empty 'noise description' field
                    Toast.makeText(AddNewTriggerNoiseActivity.this, "'Noise Description' field is empty!", Toast.LENGTH_LONG).show();
                // action of all fields are completed
                } else {
                    // retrieval of input field data on button click
                    devicePassword = devicePasswordInput.getText().toString().trim();
                    noiseDescription = noiseDescriptionInput.getText().toString().trim();

                    // Accessing SharedPreferences Data (Stored Device RBP IP Address)
                    SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
                    ipAddress = ipAddressSharedPref.getString("rbp_ip_address", "");

                    // Accessing SharedPreferences Data (Stored Device ID)
                    SharedPreferences deviceIDSharedPref = getSharedPreferences("device_id_shared_pref", Context.MODE_PRIVATE);
                    deviceId = deviceIDSharedPref.getString("rbp_device_id", "");

                    new AsyncTask<Integer, Void, Void>() {
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
            }
        });


        ImageButton btnSaveToDictionary = findViewById(R.id.add_noise_dictionary_imgBtn);
        btnSaveToDictionary.setOnClickListener(new View.OnClickListener() {
            //start execution of ssh commands
            @Override
            public void onClick(View v){
                // check if both fields are empty
                if ((devicePasswordInput.length() == 0) && (noiseDescriptionInput.length()== 0)) {
                    // messsage to highlight empty fields
                    Toast.makeText(AddNewTriggerNoiseActivity.this, "No password or noise description entered!", Toast.LENGTH_LONG).show();
                    // check if text has been entered in the password field
                } else if (devicePasswordInput.length() == 0) {
                    // messsage to highlight empty password field
                    Toast.makeText(AddNewTriggerNoiseActivity.this, "No password entered!", Toast.LENGTH_LONG).show();
                    // check if text has been entered in the 'noise description' field
                } else if (noiseDescriptionInput.length()== 0) {
                    // messsage to highlight empty 'noise description' field
                    Toast.makeText(AddNewTriggerNoiseActivity.this, "'Noise Description' field is empty!", Toast.LENGTH_LONG).show();
                    // action of all fields are completed
                } else {
                    // retrieval of input field data on button click
                    devicePassword = devicePasswordInput.getText().toString().trim();
                    noiseDescription = noiseDescriptionInput.getText().toString().trim();

                    // Accessing SharedPreferences Data (Stored Device RBP IP Address)
                    SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
                    ipAddress = ipAddressSharedPref.getString("rbp_ip_address", "");

                    // Accessing SharedPreferences Data (Stored Device ID)
                    SharedPreferences deviceIDSharedPref = getSharedPreferences("device_id_shared_pref", Context.MODE_PRIVATE);
                    deviceId = deviceIDSharedPref.getString("rbp_device_id", "");


                    new AsyncTask<Integer, Void, Void>() {
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
            InputStream errStreamAddNoise = channel.getExtInputStream();
            // connect to channel
            channel.connect();

            // debugging logs TAG strings
            String TAG_errStr = "hh_err_log";
            String TAG_errStr_exception = "hh_err_exce";
            // command line output comparison String (to trigget notification)
            String cmdRecordMsg = "INFO:sopare.recorder:start endless recording";

            // Reading command line output (from Raspberry Pi)
            BufferedReader readerAddNoise = new BufferedReader(new InputStreamReader(errStreamAddNoise));
            StringBuilder outputAddNoise = new StringBuilder();
            String lineAddNoise;
            while ((lineAddNoise = readerAddNoise.readLine()) != null) {
                // append output to StringBuilder
                outputAddNoise.append(lineAddNoise);
                // output to log (debugging)
                Log.i(TAG_errStr, lineAddNoise);
                // comparison statement to output message to start recording
                if(lineAddNoise.equals(cmdRecordMsg)) {
                    // Snackbar to prompt user
                    Snackbar.make(findViewById(android.R.id.content),
                            "Record Noise / Phrase", Snackbar.LENGTH_INDEFINITE)
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
    public void executeSSHCommandSaveNoiseToDictionary() throws Exception {
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


            // Obtain command line output as String (via InputStream)
            InputStream errStreamSaveNoise = channel.getInputStream();
            // connect to channel
            channel.connect();

            // debugging logs TAG strings
            String TAG_errStrSaveNoise = "hh_err_log_save";
            String TAG_errStr_exceptionSaveNoise = "hh_err_exce_save";
            final String TAG_firebase_add_success = "TAG_db_add_success";
            final String TAG_firebase_add_failure = "TAG_db_read_failure";
            // command line output comparison String (to trigget notification)
            String cmdNoiseSavedMsg = "recreating dictionary from raw input files...";

            // Reading command line output (from Raspberry Pi)
            BufferedReader reader = new BufferedReader(new InputStreamReader(errStreamSaveNoise));
            StringBuilder outputSaveNoise = new StringBuilder();
            String lineSaveNoise;
            while ((lineSaveNoise = reader.readLine()) != null) {
                // append output to StringBuilder
                outputSaveNoise.append(lineSaveNoise);
                // output to log (debugging)
                Log.i(TAG_errStrSaveNoise, lineSaveNoise);
                // comparison statement to output message to start recording
                if(lineSaveNoise.equals(cmdNoiseSavedMsg)) {
                    // Create a new noise to add to DB
                    Map<String, Object> newNoise = new HashMap<>();
                    newNoise.put("noise", noiseDescription);

                    // Add a new Fireatore DB document with a generated ID
                    noisesAddedDB.collection("trigger_noises")
                            .add(newNoise)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG_firebase_add_success, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    // Snackbar to indicate noise successfully saved to dictionary
                                    Snackbar.make(findViewById(android.R.id.content),
                                            "Noise Successfully Saved to Dictionary and Database", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG_firebase_add_failure, "Error adding document", e);// Snackbar to indicate noise successfully saved to dictionary
                                    Snackbar.make(findViewById(android.R.id.content),
                                            "Error adding noise to database", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });


                }
            }
            // InputStream exception try/catch
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                Log.e(TAG_errStrSaveNoise, TAG_errStr_exceptionSaveNoise);
            }
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

    /**
     * Method to display tooltip (Info on adding a new trigger noise) on image button click
     * @param view
     */
    public void displayNewNoiseTooltip(View view) {

        // set Alert Dialog box to confirm system shutdown
        AlertDialog.Builder addNewNoiseToolTip = new AlertDialog.Builder(AddNewTriggerNoiseActivity.this);
        // allow alert dialog to be cancelled by clicking area outside of the box
        addNewNoiseToolTip.setCancelable(true);
        // set messages
        addNewNoiseToolTip.setTitle("Adding New Trigger Noise");
        addNewNoiseToolTip.setMessage("To add a new trigger noise (i.e. speech or a consistent, repeatable sound) enter the required details, press record and follow the on-screen prompt.\n\nEach noise should be recorded at least 3 times before saving to the device dictionary.\n\nIf recording speech, record one word only.");
        // set negative 'cancel' button
        addNewNoiseToolTip.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                dialogInterface.cancel();
            }
        });

        addNewNoiseToolTip.show();

    }

}
