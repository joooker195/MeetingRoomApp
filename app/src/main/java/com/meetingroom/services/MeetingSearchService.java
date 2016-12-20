package com.meetingroom.services;

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
import com.meetingroom.variables.MeetingRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ксю on 20.12.2016.
 */
public class MeetingSearchService extends IntentService {
    public MeetingSearchService() {
        super("MeetingSearchService");
    }

    Map<String, Map<String, String>> mapMeetings;

    List<MeetingRow> listMeetings = new ArrayList<>();

    MeetingListListener meetingListener;

    private String mDesc;


    public static final String ACTION_MYINTENTSERVICE = "com.meetingroomSearch.RESPONSE";
    public static final String NETWORK = "NETWORK";
    public static final String MEETINGS = "MEETINGS";
    public static final String DESC = "DESC";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Intent responseIntent = new Intent();

        mDesc = intent.getStringExtra(DESC);

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {

            DatabaseReference a = FirebaseDatabase.getInstance().getReference();
            meetingListener = new MeetingListListener(a);
            a.addValueEventListener(meetingListener);
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
            mapMeetings = (Map<String, Map<String, String>>) dataSnapshot.child("Meetings").getValue();

            if(!mDesc.equals("")) {

                for (int i = 0; i < mapMeetings.keySet().toArray().length; i++) {
                    MeetingRow meeting = new MeetingRow();
                    String desc = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("desc");
                    if(desc.equals(mDesc)) {
                        String title = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("title");
                        String key = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("key");
                        String[] date = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("begin").split(" ");
                        String[] dateEnd = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("end").split(" ");
                        meeting.setTitle(title);
                        meeting.setDesc(desc);
                        meeting.setKey(key);
                        meeting.setDate(date[0]);
                        meeting.setTimeBegin(date[1]);
                        meeting.setDateEnd(dateEnd[0]);
                        meeting.setTimeEnd(dateEnd[1]);
                        listMeetings.add(meeting);
                    }
                }
            }
            else
            {
                for (int i = 0; i < mapMeetings.keySet().toArray().length; i++) {
                    MeetingRow meeting = new MeetingRow();
                    String desc = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("desc");
                    String title = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("title");
                    String key = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("key");
                    String[] date = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("begin").split(" ");
                    String[] dateEnd = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("end").split(" ");
                    meeting.setTitle(title);
                    meeting.setDesc(desc);
                    meeting.setKey(key);
                    meeting.setDate(date[0]);
                    meeting.setTimeBegin(date[1]);
                    meeting.setDateEnd(dateEnd[0]);
                    meeting.setTimeEnd(dateEnd[1]);
                    listMeetings.add(meeting);
                }
            }

            mRef.removeEventListener(this);

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
