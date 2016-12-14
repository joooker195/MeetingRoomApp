package com.meetingroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private RecyclerView mMeetingList;

    private DatabaseReference mDatabase;

    private Firebase fb;

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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Meetings");


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
                            ArrayList<MeetingRow> list = new ArrayList<>();

                            for(int i=0; i<map.keySet().toArray().length; i++)
                            {
                                MeetingRow a = new MeetingRow();
                                String desc = map.get(map.keySet().toArray()[i].toString()).get("desc");
                                String title = map.get(map.keySet().toArray()[i].toString()).get("title");
                                if(desc.equals(mEditText.getText().toString())) {
                                    a.setTitle(title);
                                    a.setDesc(desc);
                                    list.add(a);
                                }
                            }

                            RVAdapter adapter = new RVAdapter(list);
                            mMeetingList.setAdapter(adapter);

                        }
                        catch (Exception e)
                        {

                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }

                });


            }
        });


    }



    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
        ArrayList<MeetingRow> meetings = new ArrayList<>();

        public RVAdapter(ArrayList<MeetingRow> meetings)
        {
            this.meetings = meetings;
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row, parent, false);
            PersonViewHolder pvh = new PersonViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(PersonViewHolder holder, int position) {
            holder.mTitle.setText(meetings.get(position).getTitle());
            holder.mDesc.setText(meetings.get(position).getDesc());

        }

        @Override
        public int getItemCount() {
            return meetings.size();
        }

        public  class PersonViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView mTitle;
            TextView mDesc;
            PersonViewHolder(View itemView) {
                super(itemView);
//                cv = (CardView)itemView.findViewById(R.id.meeting_list_search);
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
