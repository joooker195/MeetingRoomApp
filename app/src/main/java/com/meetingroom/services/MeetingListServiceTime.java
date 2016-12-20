package com.meetingroom.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetingroom.variables.MeetingRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ксю on 17.12.2016.
 */
public class MeetingListServiceTime extends IntentService
{

    Map<String, Map<String, String>> map = new HashMap<>();
    List<MeetingRow> listMeetings = new ArrayList<>();

    MeetingListListener meetingListener;
    private  Firebase mRef;

    private Calendar date;
    private int mYear;
    private int mMonth;
    private int mDay;

    public String mDate;
    {
        date = Calendar.getInstance();
        mYear = date.get(Calendar.YEAR);
        mMonth = date.get(Calendar.MONTH);
        mDay = date.get(Calendar.DAY_OF_MONTH);
        mDate = new StringBuilder()
                .append(mDay).append(".")
                .append(mMonth+1).append(".")
                .append(mYear).toString();
    }

    public static final String ACTION_MYINTENTSERVICE = "com.meetingroom.RESPONSE";
    public static final String NETWORK = "NETWORK";
    public static final String MEETINGS = "MEETINGS";

    public MeetingListServiceTime() {
        super("MeetingListServiceTime");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        mRef = new Firebase("https://meeting-room-3a41e.firebaseio.com/");
        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {

            DatabaseReference a = FirebaseDatabase.getInstance().getReference();
            meetingListener = new MeetingListListener(a);
            a.child("Meetings").addValueEventListener(meetingListener);
            while(true){
                try {
                    Thread.sleep(60000);
                    sendIntent();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"0");
            responseIntent.putExtra(MEETINGS, (Serializable) listMeetings);
            sendBroadcast(responseIntent);
            stopSelf();
        }
    }

    public void sendIntent(){
        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_MYINTENTSERVICE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(MEETINGS, (Serializable) listMeetings);
        responseIntent.putExtra(NETWORK,"1");
        sendBroadcast(responseIntent);
    }


    public class MeetingListListener implements ValueEventListener
    {
        private DatabaseReference mRef;

        public MeetingListListener(DatabaseReference ref)
        {
            this.mRef = ref;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            map = (Map<String, Map<String, String>>) dataSnapshot.getValue();

            for (int i = 0; i < map.keySet().toArray().length; i++) {
                MeetingRow meeting = new MeetingRow();
                String desc = map.get(map.keySet().toArray()[i].toString()).get("desc");
                String title = map.get(map.keySet().toArray()[i].toString()).get("title");
                String key = map.get(map.keySet().toArray()[i].toString()).get("key");
                String[] date = map.get(map.keySet().toArray()[i].toString()).get("begin").split(" ");
                if (date[0].equals(mDate)) {
                    meeting.setTitle(title);
                    meeting.setDesc(desc);
                    meeting.setKey(key);
                    meeting.setDate(date[0]);
                    listMeetings.add(meeting);
                }
            }
            mRef.child("Meetings").removeEventListener(this);

            Intent responseIntent = new Intent();
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK, "1");
            responseIntent.putExtra(MEETINGS, (Serializable) listMeetings);
            sendBroadcast(responseIntent);
            stopSelf();
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {
            Log.e("MLSError:", databaseError.getMessage());

        }
    }
}
