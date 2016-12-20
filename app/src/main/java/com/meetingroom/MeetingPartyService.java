package com.meetingroom;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.firebase.client.Firebase;
import com.meetingroom.variables.MainVariables;

/**
 * Created by Ксю on 19.12.2016.
 */
public class MeetingPartyService extends IntentService {
    public MeetingPartyService() {
        super("MeetingPartyService");

        Log.v("E_VALUE", "Meeting Party Service Create");
    }
    String name = "";
    String prof = "";
    String key = "";

    public static final String ACTION_MYINTENTSERVICE = "com.meetingroomParty.RESPONSE";
    public static final String NETWORK = "NETWORK";
    public static final String NAME = "NAME";
    public static final String PROF = "PROF";
    public static final String KEY = "KEY";


    @Override
    protected void onHandleIntent(Intent intent)
    {
        Intent responseIntent = new Intent();

        name = intent.getStringExtra(NAME);
        prof = intent.getStringExtra(PROF);
        key = intent.getStringExtra(KEY);

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {

            String newKey = MainVariables.getKey();

           /* DatabaseReference partysName = FirebaseDatabase.getInstance().getReference().child("Meetings")
                    .child("n_"+key).child("partys").child("p_"+ newKey).child("name");
            partysName.push().setValue(name);
            DatabaseReference partysProf = FirebaseDatabase.getInstance().getReference().child("Meetings")
                    .child("n_"+key).child("partys").child("p_"+ newKey).child("prof");
            partysProf.setValue(prof);
            partysProf.push();*/
            Firebase f = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/n_"+key+"/partys/p_"+newKey);
            f.child("name").setValue(name);
            f.child("prof").setValue(prof);

            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"1");
            sendBroadcast(responseIntent);
            stopSelf();

        }
        else {
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"0");
            sendBroadcast(responseIntent);
            stopSelf();
        }

    }
}
