package com.example.safeslotting;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

public class GeofenceBroadReciever extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceive";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(final Context context, final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast
        DatabaseReference Post= FirebaseDatabase.getInstance().getReference().child("Post");
        Post.child("-MCZgQZv1DtDwbIjUbWB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer count =snapshot.child("count").getValue(Integer.class);
                Log.i("TAG =>", count +" ");

                //count=count+1;

                NotificationHelper notificationHelper=new NotificationHelper(context);
                GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);


                if (geofencingEvent.hasError()) {
                    Log.d(TAG, "onReceive: Error receiving geofence event...");
                    return;
                }

                List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
                for (Geofence geofence: geofenceList) {
                    Log.d(TAG, "onReceive: " + geofence.getRequestId());
                }

                int transitionType = geofencingEvent.getGeofenceTransition();

                switch (transitionType) {
                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                        // Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                        // notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity.class);
                        break;
                    case Geofence.GEOFENCE_TRANSITION_DWELL:
                        // Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();

                        notificationHelper.sendHighPriorityNotification("Number : "+ count, "", MapsActivity.class);
                        HashMap<String,Object>  map=new HashMap<>();
                        map.put("count",count+1);


                        FirebaseDatabase.getInstance().getReference().child("Post")
                                .child("-MCZgQZv1DtDwbIjUbWB")
                                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        //  Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                        //  notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
                        break;
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
