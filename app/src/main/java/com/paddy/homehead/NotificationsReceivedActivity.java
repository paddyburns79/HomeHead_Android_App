package com.paddy.homehead;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NotificationsReceivedActivity extends AppCompatActivity {

    DatabaseHelper notificationsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_received);

        // link Listview to DB
        ListView listView = findViewById(R.id.notifications_listview);
        notificationsDB = new DatabaseHelper(this);

        //populate an ArrayList<String> from the database
        ArrayList<String> theList = new ArrayList<>();
        // get contents
        Cursor data = notificationsDB.getListContents();
        if(data.getCount() == 0){
            Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
        }else{
            while(data.moveToNext()){
                theList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList);
                listView.setAdapter(listAdapter);
            }
        }

    }
}
