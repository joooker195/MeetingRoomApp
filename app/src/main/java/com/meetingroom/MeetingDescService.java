package com.meetingroom;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetingroom.variables.MeetingPartys;
import com.meetingroom.variables.MeetingRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ксю on 19.12.2016.
 */

public class MeetingDescService extends IntentService {

    Map<String, Map<String, String>> mapMeetings;
    Map<String, Map<String, String>> mapPartys;
    MeetingRow meeting = new MeetingRow();
    List<MeetingPartys> listPartys = new ArrayList<>();
    String key = "";

    MeetingDescListener meetingListener;

    public static final String ACTION_MYINTENTSERVICE = "com.meetingroomDesc.RESPONSE";
    public static final String NETWORK = "NETWORK";
    public static final String MEETING = "MEETING";
    public static final String PARTYS = "PARTYS";
    public static final String KEY = "KEY";


    public MeetingDescService() {
        super("MeetingDescService");

        Log.v("E_VALUE", "Meeting Desc Service Create");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Intent responseIntent = new Intent();

        key = intent.getStringExtra(KEY);

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {

            DatabaseReference a = FirebaseDatabase.getInstance().getReference();
            meetingListener = new MeetingDescListener(a);
            a.addValueEventListener(meetingListener);
        }
        else {
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"0");
            responseIntent.putExtra(MEETING, (Serializable) meeting);
            sendBroadcast(responseIntent);
            stopSelf();
        }
    }

    public class MeetingDescListener implements ValueEventListener
    {
        private DatabaseReference mRef;

        public MeetingDescListener(DatabaseReference ref)
        {
            this.mRef = ref;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            mapMeetings = (Map<String, Map<String, String>>) dataSnapshot.child("Meetings").getValue();

            String desc = mapMeetings.get("n_" + key).get("desc").toString();
            String title = mapMeetings.get("n_" + key).get("title").toString();
            String beginDate = mapMeetings.get("n_" + key).get("begin").toString();
            String endDate = mapMeetings.get("n_" + key).get("end").toString();
            String priority = mapMeetings.get("n_" + key).get("priority").toString();

            meeting.setTitle(title);
            meeting.setDesc(desc);
            meeting.setDate(beginDate);
            meeting.setDateEnd(endDate);
            meeting.setPriority(priority);


            if(dataSnapshot.child("Meetings").child("n_"+key).child("partys") != null)
            {
                mapPartys = (Map<String, Map<String, String>>) dataSnapshot.child("Meetings").child("n_"+key).child("partys").getValue();
                MeetingPartys party = new MeetingPartys();
                for(int i=0; i< mapPartys.size(); i++) {
                    party.setName(mapPartys.get("p_"+i).get("name"));
                    party.setProf(mapPartys.get("p_" + i).get("prof"));
                    listPartys.add(party);
                }
            }

            mRef.removeEventListener(this);

            Intent responseIntent = new Intent();
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK, "1");
            responseIntent.putExtra(MEETING, (Serializable) meeting);
            responseIntent.putExtra(PARTYS, (Serializable) listPartys);
            sendBroadcast(responseIntent);
            stopSelf();
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {
            Log.e("MDSError:", databaseError.getMessage());

        }
    }
}
