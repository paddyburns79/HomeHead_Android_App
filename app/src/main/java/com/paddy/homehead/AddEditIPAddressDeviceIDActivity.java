package com.paddy.homehead;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditIPAddressDeviceIDActivity extends AppCompatActivity {

    String savedIpAddress, savedDeviceID;

    // Textview to hold IP Address retrieved from SharedPreferences
    TextView ipAddressView, deviceIDView;

    // Input values to hold input field data
    EditText ipAddressInput, deviceIDInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ipaddress);

        // Calling method to display the current Device ID and IP Address SharedPreference stored values
        showDeviceIDData();
        showIPAddressData();

        // linking input values to input field
        ipAddressInput = (EditText) findViewById(R.id.input_add_IP_address);
        deviceIDInput = (EditText) findViewById(R.id.input_add_device_ID);
    }

    /**
     * Method to save IP Address data input to SharedPreferences on button click
     * @param view
     */
    public void saveIpAddressData(View view) {

    // checking if new IP Address entered matches current before writing to Shared Preferences file
    if (ipAddressInput.getText().toString().equalsIgnoreCase(savedIpAddress)) {
        // Toast popup to confirm IP Address entered matches that already saved
        Toast.makeText(this, "IP Address Entered Already Saved", Toast.LENGTH_LONG).show();
    // checking text has been entered in the input field
    } else if (ipAddressInput.length() ==0) {
        // messsage to highlight empty field
        Toast.makeText(AddEditIPAddressDeviceIDActivity.this, "No IP Address entered!", Toast.LENGTH_LONG).show();
    } else {
        // setting Shared Preferences file (Mode Private so only this app can access it)
        SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
        // write IP Address input data to SharedPreferences
        SharedPreferences.Editor editor = ipAddressSharedPref.edit();
        editor.putString("rbp_ip_address", ipAddressInput.getText().toString());
        editor.apply();
        // Toast popup to confirm successful save operation
        Toast.makeText(this, "IP Address Saved", Toast.LENGTH_LONG).show();
        // clear input field
        ipAddressInput.getText().clear();
    }
    // calling method to refresh data displayed on button click
    showIPAddressData();

    }

    /**
     * Method to save Device ID data input to SharedPreferences on button click
     * @param view
     */
    public void saveDeviceIDData(View view) {

        // checking if new IP Address entered matches current before writing to Shared Preferences file
        if (deviceIDInput.getText().toString().equalsIgnoreCase(savedDeviceID)) {
            // Toast popup to confirm IP Address entered matches that already saved
            Toast.makeText(this, "Device ID Entered Already Saved", Toast.LENGTH_LONG).show();
            // checking text has been entered in the input field
        } else if (deviceIDInput.length() ==0) {
            // messsage to highlight empty field
            Toast.makeText(AddEditIPAddressDeviceIDActivity.this, "No Device ID entered!", Toast.LENGTH_LONG).show();
        } else {
            // setting Shared Preferences file (Mode Private so only this app can access it)
            SharedPreferences deviceIDSharedPref = getSharedPreferences("device_id_shared_pref", Context.MODE_PRIVATE);
            // write IP Address input data to SharedPreferences
            SharedPreferences.Editor editor = deviceIDSharedPref.edit();
            editor.putString("rbp_device_id", deviceIDInput.getText().toString());
            editor.apply();
            // Toast popup to confirm successful save operation
            Toast.makeText(this, "Device ID Saved", Toast.LENGTH_LONG).show();
            // clear input field
            deviceIDInput.getText().clear();
        }
        // calling method to refresh data displayed on button click
        showDeviceIDData();

    }

    /**
     * Method to display the current IP address data saved in SharedPreferences
     */
    public void showIPAddressData() {
         // linking textview to SharedPreferences Data
        ipAddressView = (TextView) findViewById(R.id.textView_current_IP_address);

        // Accessing SharedPreferences Data (Stored Device RBP IP Address)
        SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);
        String rbpIpAddress = ipAddressSharedPref.getString("rbp_ip_address", "");
        if (rbpIpAddress.isEmpty()) {
            ipAddressView.setText(R.string.No_IP_Address);
        } else {
            ipAddressView.setText(rbpIpAddress);
            savedIpAddress = rbpIpAddress;
        }
    }

    /**
     * Method to display the current Device ID data saved in SharedPreferences
     */
    public void showDeviceIDData() {
        // linking textview to SharedPreferences Data
        deviceIDView = (TextView) findViewById(R.id.textView_current_device_ID);

        // Accessing SharedPreferences Data (Stored Device ID)
        SharedPreferences deviceIDSharedPref = getSharedPreferences("device_id_shared_pref", Context.MODE_PRIVATE);
        String rbpDeviceID = deviceIDSharedPref.getString("rbp_device_id", "");
        if (rbpDeviceID.isEmpty()) {
            deviceIDView.setText(R.string.no_device_id_added);
        } else {
            deviceIDView.setText(rbpDeviceID);
            savedDeviceID = rbpDeviceID;
        }
    }

    /**
     * Method to display tooltip (Info on adding IP Address) on image button click
     * @param view
     */
    public void displayIpTooltip(View view) {

        // set Alert Dialog box to confirm system shutdown
        AlertDialog.Builder shutdownConfirm = new AlertDialog.Builder(AddEditIPAddressDeviceIDActivity.this);
        // allow alert dialog to be cancelled by clicking area outside of the box
        shutdownConfirm.setCancelable(true);
        // set messages
        shutdownConfirm.setTitle("Obtaining IP Address");
        shutdownConfirm.setMessage("The IP address of the Raspberry Pi device (format example : 192.168.x.xxx) can be obtained by typing 'hostname -I' into the command line of the device, or by using an app such as Fing (available for free from the Google Play store)");
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
