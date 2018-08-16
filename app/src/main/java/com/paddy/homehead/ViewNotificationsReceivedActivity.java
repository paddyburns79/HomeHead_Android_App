package com.paddy.homehead;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class ViewNotificationsReceivedActivity extends AppCompatActivity {

    DatabaseHelper notificationsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_received);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.homehead_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // link Listview to DB
        ListView listView = findViewById(R.id.notifications_listview);
        notificationsDB = new DatabaseHelper(this);

        //populate an ArrayList<String> from the database
        ArrayList<String> msgsReceivedArrayList = new ArrayList<>();
        // get contents
        Cursor data = notificationsDB.getListContents();
        if(data.getCount() == 0){
            Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
        }else{
            while(data.moveToNext()){
                // outputs Auto Incremented ID Value
                //msgsReceivedArrayList.add((data.getString(0)));
                // outputs message body
                msgsReceivedArrayList.add(data.getString(1));

                ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,msgsReceivedArrayList);
                listView.setAdapter(listAdapter);
            }
        }

    }
}
