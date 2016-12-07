package com.meetingroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class MeetingDescActivity extends AppCompatActivity {


    private Firebase mRef;
    private Map<String, String> mMeetingPartysList;
    private Map<String, Map<String,String>> mMeeting;
    private Map<String,Map<String,String>> mMeetingsList;

    public static  String KEY = "";

    private TextView mTitle;
    private TextView mDesc;
    private ListView mPrtys;
    private TextView mBegin;
    private TextView mEnd;
    private TextView mPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_desc);
        Firebase.setAndroidContext(this);

        mTitle = (TextView) findViewById(R.id.desc_title);
        mDesc = (TextView) findViewById(R.id.desc_description);
        mBegin = (TextView) findViewById(R.id.desc_begin);
        mEnd = (TextView) findViewById(R.id.desc_end);
        mPriority = (TextView) findViewById(R.id.desc_priority);
        mPrtys = (ListView) findViewById(R.id.desc_party);


        mRef = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Map<String,String>> map = dataSnapshot.getValue(Map.class);
                mTitle.setText(map.get("n_"+KEY).get("title").toString());
                mDesc.setText(map.get("n_"+KEY).get("desc").toString());
                mBegin.setText("Начало: "+map.get("n_"+KEY).get("begin").toString());
                mEnd.setText("Конец: "+ map.get("n_"+KEY).get("end").toString());
                mPriority.setText("Приоритет: "+map.get("n_"+KEY).get("priority").toString());


                Log.v("E_VALUE", "Message:" + KEY);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.desc_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_back) {
            Intent intent = new Intent(MeetingDescActivity.this, MeetingListActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
