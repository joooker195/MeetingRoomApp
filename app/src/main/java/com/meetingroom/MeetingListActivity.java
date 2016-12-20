package com.meetingroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.meetingroom.adapter.RVAdapter;
import com.meetingroom.services.MeetingListService;
import com.meetingroom.services.MeetingListServiceTime;
import com.meetingroom.variables.MeetingRow;

import java.util.ArrayList;
import java.util.Calendar;

public class MeetingListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private RecyclerView mMeetingList;

    private Calendar date;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mDate;

    private TextView dateText;

    ArrayList<MeetingRow> m = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);

        mMeetingList = (RecyclerView) findViewById(R.id.meeting_list);
        mMeetingList.setHasFixedSize(true);
        mMeetingList.setLayoutManager(new LinearLayoutManager(this));


        date = Calendar.getInstance();
        mYear = date.get(Calendar.YEAR);
        mMonth = date.get(Calendar.MONTH);
        mDay = date.get(Calendar.DAY_OF_MONTH);
        dateText = (TextView) findViewById(R.id.date);
        mDate = new StringBuilder()
                .append(mDay).append(".")
                .append(mMonth + 1).append(".")
                .append(mYear).toString();

        dateText.setText(mDate);

        updateMeetingList();

        MeetingBroadcastReceiver meetingBroadcast = new MeetingBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                MeetingListService.ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(meetingBroadcast, intentFilter);

        MeetingListBroadcastReceiver meetingListBroadcast = new MeetingListBroadcastReceiver();
        IntentFilter intentFilterTime = new IntentFilter(
                MeetingListService.ACTION_MYINTENTSERVICE);
        intentFilterTime.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(meetingListBroadcast, intentFilterTime);


        updateTimeMeetingList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    public class MeetingBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("NETWORK").equals("1")) {
                m = (ArrayList<MeetingRow>) intent.getSerializableExtra(MeetingListService.MEETINGS);
                RVAdapter adapter = new RVAdapter(m, MeetingListActivity.this);
                mMeetingList.setAdapter(adapter);
                Toast.makeText(MeetingListActivity.this, "Meetings update", Toast.LENGTH_LONG).show();
            } else Toast.makeText(context, "Network not found!", Toast.LENGTH_LONG).show();

        }
    }

    public class MeetingListBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("NETWORK").equals("1")) {
                m = (ArrayList<MeetingRow>) intent.getSerializableExtra(MeetingListServiceTime.MEETINGS);
                RVAdapter adapter = new RVAdapter(m, MeetingListActivity.this);
                mMeetingList.setAdapter(adapter);
                Toast.makeText(MeetingListActivity.this, "Список обновлен", Toast.LENGTH_LONG).show();
            } else Toast.makeText(context, "Network not found!", Toast.LENGTH_LONG).show();

        }
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
            updateMeetingList();
            // return true;
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

    public void updateMeetingList()
    {
        Intent intent = new Intent(MeetingListActivity.this, MeetingListService.class);
        startService(intent);
    }

    public void updateTimeMeetingList()
    {
        Intent intent = new Intent(MeetingListActivity.this, MeetingListServiceTime.class);
        startService(intent);
    }

}
