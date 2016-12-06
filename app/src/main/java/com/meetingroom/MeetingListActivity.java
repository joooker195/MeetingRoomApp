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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MeetingListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    private RecyclerView mMeetingList;

    private DatabaseReference mDatabase;

    private Calendar date;
    private int mYear;
    private int mMonth;
    private int mDay;

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
        dateText.setText(new StringBuilder()
                .append(mMonth + 1).append(".")
                .append(mDay).append(".")
                .append(mYear).append(" "));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<MeetingRow, MeetingViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<MeetingRow, MeetingViewHolder>(
                        MeetingRow.class,
                        R.layout.meeting_row,
                        MeetingViewHolder.class,
                        mDatabase) {
            @Override
            protected void populateViewHolder(MeetingViewHolder viewHolder, MeetingRow model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
            }
        };
        mMeetingList.setAdapter(firebaseRecyclerAdapter);
    }

    public void onClickNext(View view)
    {
        Intent intent= new Intent(MeetingListActivity.this, MeetingDescActivity.class);
        startActivity(intent);

    }


    private static class MeetingViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public MeetingViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title)
        {
            TextView meeting_title = (TextView) mView.findViewById(R.id.meeting_title);
            meeting_title.setText(title);
        }
        public void setDesc(String desc)
        {
            TextView meeting_desc = (TextView) mView.findViewById(R.id.meeting_desc);
            meeting_desc.setText(desc);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.meeting_list, menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
                Intent intent = new Intent(MeetingListActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_add_group:
                break;
            case R.id.nav_group:
                break;
            case R.id.nav_search:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_faq:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
