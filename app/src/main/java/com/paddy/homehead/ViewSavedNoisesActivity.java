package com.paddy.homehead;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ViewSavedNoisesActivity extends AppCompatActivity {


    private ListView savedNoisesList;
    // debugging TAG
    private static final String TAG = "dbNoiseList";

    private List<String> noisesToAdd = new ArrayList<>();
   // ListView savedNoisesList = findViewById(R.id.saved_noises_listview);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_noises);

        // identify ListView and assign to var
        final ListView listView = findViewById(R.id.saved_noises_listview);

        // instance of Firestore DB
        FirebaseFirestore dB = FirebaseFirestore.getInstance();

        // query Firestore database
        dB.collection("trigger_noises").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // obtain database records
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // check if data is returned
                        if(document.getData().isEmpty()) {
                            Toast.makeText(ViewSavedNoisesActivity.this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, document.getId());
                            noisesToAdd.add(document.getString("noise"));
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                // create ArrayAdapter and link to ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewSavedNoisesActivity.this,android.R.layout.simple_selectable_list_item,noisesToAdd);
                listView.setAdapter(adapter);
            }
        });


    }



}
