package com.meetingroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {


    private RecyclerView mMeetingList;

    private EditText mEditText;
    private Button mButtonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mMeetingList = (RecyclerView) findViewById(R.id.meeting_list_search);
        mMeetingList.setHasFixedSize(true);
        mMeetingList.setLayoutManager(new LinearLayoutManager(this));

        mEditText = (EditText) findViewById(R.id.search_edit);
        mButtonSearch = (Button) findViewById(R.id.search);
    }


    protected void onStart() {

        super.onStart();
        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Firebase mRef = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/");
                mRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {

                            Map<String, Map<String, String>> map = dataSnapshot.getValue(Map.class);
                            ArrayList<MeetingRow> listMeetings = new ArrayList<>();

                            for(int i=0; i<map.keySet().toArray().length; i++)
                            {
                                MeetingRow meeting = new MeetingRow();
                                String desc = map.get(map.keySet().toArray()[i].toString()).get("desc");
                                String title = map.get(map.keySet().toArray()[i].toString()).get("title");
                                String key = map.get(map.keySet().toArray()[i].toString()).get("key");
                                if(desc.equals(mEditText.getText().toString())) {
                                    meeting.setTitle(title);
                                    meeting.setDesc(desc);
                                    meeting.setKey(key);
                                    listMeetings.add(meeting);
                                }
                            }

                            RVAdapter adapter = new RVAdapter(listMeetings);
                            mMeetingList.setAdapter(adapter);

                        }
                        catch (Exception e)
                        {
                            Log.e("E_VALUE", e.getMessage());
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }

                });


            }
        });


    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MeetingViewHolder>{

        ArrayList<MeetingRow> meetings = new ArrayList<>();

        public RVAdapter(ArrayList<MeetingRow> meetings)
        {
            this.meetings = meetings;
        }

        @Override
        public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View mVewCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row, parent, false);
            MeetingViewHolder mMeetingViewHolder = new MeetingViewHolder(mVewCard);


            return mMeetingViewHolder;
        }

        @Override
        public void onBindViewHolder(MeetingViewHolder holder, final int position) {
            holder.mTitle.setText(meetings.get(position).getTitle());
            holder.mDesc.setText(meetings.get(position).getDesc());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MeetingDescActivity.KEY = meetings.get(position).getKey();

                    Intent intent= new Intent(SearchActivity.this, MeetingDescActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

        }

        @Override
        public int getItemCount() {
            return meetings.size();
        }

        public  class MeetingViewHolder extends RecyclerView.ViewHolder {
            TextView mTitle;
            TextView mDesc;
            MeetingViewHolder(View itemView) {
                super(itemView);
                mTitle = (TextView)itemView.findViewById(R.id.meeting_title);
                mDesc = (TextView)itemView.findViewById(R.id.meeting_desc);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_meeting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_searching_back) {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
