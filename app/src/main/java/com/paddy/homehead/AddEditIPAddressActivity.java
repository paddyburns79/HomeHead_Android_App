package com.paddy.homehead;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditIPAddressActivity extends AppCompatActivity {

    // Textview to hold IP Address retrieved from SharedPreferences
    TextView ipAddressView;

    // Strings to accept user input data
    String deviceIpAddress;

    // Input values to hold input field data
    EditText ipAddressInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ipaddress);

        // linking input value to input field
        ipAddressInput = (EditText) findViewById(R.id.input_add_IP_address);
    }

    /**
     * Method to save IP Address data input to SharedPreferences on button click
     * @param view
     */
    public void saveIpAddressData(View view) {
        // setting Shared Preferences file (Mode Private so only this app can access it)
        SharedPreferences ipAddressSharedPref = getSharedPreferences("device_ip_shared_pref", Context.MODE_PRIVATE);

        // write IP Address input data to SharedPreferences
        SharedPreferences.Editor editor = ipAddressSharedPref.edit();
        editor.putString("rbp_ip_address", ipAddressInput.getText().toString());
        editor.apply();

        // Toast popup to confirm siccessful save operation
        Toast.makeText(this, "IP Address Saved", Toast.LENGTH_LONG).show();

        // calling method to refresh data displayed on button click
        showIPAddressData();
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
        ipAddressView.setText(rbpIpAddress);
    }
}
