package com.meetingroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetingroom.adapter.RVAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MeetingListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private RecyclerView mMeetingList;
    private DatabaseReference mDatabase;

    private Calendar date;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mDate;

    private TextView dateText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);

        mMeetingList = (RecyclerView) findViewById(R.id.meeting_list);
        mMeetingList.setHasFixedSize(true);
        mMeetingList.setLayoutManager(new LinearLayoutManager(this));


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Meetings");

        date = Calendar.getInstance();
        mYear = date.get(Calendar.YEAR);
        mMonth = date.get(Calendar.MONTH);
        mDay = date.get(Calendar.DAY_OF_MONTH);
        dateText= (TextView) findViewById(R.id.date);
        mDate = new StringBuilder()
                .append(mDay).append(".")
                .append(mMonth+1).append(".")
                .append(mYear).toString();

        dateText.setText(mDate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Firebase mRef = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    Map<String, Map<String, String>> map = dataSnapshot.getValue(Map.class);
                    ArrayList<MeetingRow> listMeetings = new ArrayList<>();

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

                    RVAdapter adapter = new RVAdapter(listMeetings, MeetingListActivity.this);
                    mMeetingList.setAdapter(adapter);

                } catch (Exception e) {
                    Log.e("E_VALUE", e.getMessage());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meeting_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_update)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_exit:
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(MeetingListActivity.this, MainActivity.class));
                break;
            case R.id.nav_add_group:
                startActivity(new Intent(MeetingListActivity.this, MeetingAddActivity.class));
                break;
            case R.id.nav_search:
                startActivity(new Intent(MeetingListActivity.this, MeetingSearchActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MeetingListActivity.this, SettingPreference.class));
                break;
            case R.id.nav_faq:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
