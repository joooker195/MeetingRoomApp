package com.meetingroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.meetingroom.adapter.RVAdapter;
import com.meetingroom.variables.MeetingRow;

import java.util.ArrayList;
import java.util.Map;

public class MeetingSearchActivity extends AppCompatActivity {


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

        viewCards();

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
                                if(desc.equals(mEditText.getText().toString()) || mEditText.getText().equals(""))
                                {
                                    meeting.setTitle(title);
                                    meeting.setDesc(desc);
                                    meeting.setKey(key);
                                    listMeetings.add(meeting);
                                }
                            }

                            RVAdapter adapter = new RVAdapter(listMeetings, MeetingSearchActivity.this);
                            mMeetingList.setAdapter(adapter);

                        }
                        catch (Exception e)
                        {
                            Log.e("E_VALUE", e.getMessage());
                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mButtonSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }

                });
            }
        });


    }

    public void viewCards()
    {
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

                        meeting.setTitle(title);
                        meeting.setDesc(desc);
                        meeting.setKey(key);
                        listMeetings.add(meeting);
                    }

                    RVAdapter adapter = new RVAdapter(listMeetings, MeetingSearchActivity.this);
                    mMeetingList.setAdapter(adapter);

                }
                catch (Exception e)
                {
                    Log.e("E_VALUE", e.getMessage());
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mButtonSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
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
            Intent intent = new Intent(MeetingSearchActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
