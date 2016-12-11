package com.meetingroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class MeetingAddActivity extends AppCompatActivity {

    private Button mAddMeeting;
    private Firebase mRef;
    private int count = 0;

    private EditText mEditTitle;
    private EditText mEditDesc;
    private EditText mEditDateBegin;
    private EditText mEditTimeBegin;
    private EditText mEditDateEnd;
    private EditText mEditTimeEnd;
    private EditText mEditPriority;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_add);

        mAddMeeting = (Button) findViewById(R.id.add_button);
        mEditTitle = (EditText) findViewById(R.id.add_title);
        mEditDesc = (EditText) findViewById(R.id.add_desc);
        mEditDateBegin = (EditText) findViewById(R.id.date_begin);
        mEditTimeBegin = (EditText) findViewById(R.id.time_begin);
        mEditDateEnd = (EditText) findViewById(R.id.date_end);
        mEditTimeEnd = (EditText) findViewById(R.id.time_end);
        mEditPriority = (EditText) findViewById(R.id.add_priority);

        mRef = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, String> map = dataSnapshot.getValue(Map.class);
                count = map.size();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mAddMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Firebase mChildRef = mRef.child("n_" + count);

                    if (mEditTitle.getText().toString().equals("") || mEditDesc.getText().toString().equals("")
                            || mEditDateBegin.getText().toString().equals("") || mEditTimeBegin.getText().toString().equals("")
                            || mEditDateEnd.getText().toString().equals("")  || mEditTimeEnd.getText().toString().equals("")
                            || mEditPriority.getText().toString().equals(""))
                    {
                        throw new Exception();
                    }
                    parseDate(mEditDateBegin.getText().toString(), "начала");
                    Firebase mChildRefTitle = mChildRef.child("title");
                    mChildRefTitle.setValue(mEditTitle.getText().toString());

                    Firebase mChildRefDesc = mChildRef.child("desc");
                    mChildRefDesc.setValue(mEditDesc.getText().toString());

                    Firebase mChildRefBegin = mChildRef.child("begin");
                    mChildRefBegin.setValue(mEditDateBegin.getText().toString() + " " + mEditTimeBegin.getText());

                    Firebase mChildRefEnd = mChildRef.child("end");
                    mChildRefEnd.setValue(mEditDateEnd.getText().toString() + " " + mEditTimeEnd.getText());

                    Firebase mChildRefPrior = mChildRef.child("priority");
                    mChildRefPrior.setValue(mEditPriority.getText().toString());

                    startActivity(new Intent(MeetingAddActivity.this, MeetingListActivity.class));
                }
                catch (Exception e)
                {
                    Toast.makeText(MeetingAddActivity.this, "Заполнены не все поля", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_meeting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_back_add) {
            startActivity(new Intent(MeetingAddActivity.this, MeetingListActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void parseDate(String date, String desc)
    {
        try {

            String[] parse = date.split(".");

        }
        catch (Exception e)
        {
            Toast.makeText(MeetingAddActivity.this, "Не верно заполнена дата"+desc, Toast.LENGTH_LONG).show();
        }
    }
    public void parseTime(String time, String desc)
    {
        try {

            String[] parse = time.split(":");

        }
        catch (Exception e)
        {
            Toast.makeText(MeetingAddActivity.this, "Не верно заполнено время"+desc, Toast.LENGTH_LONG).show();
        }
    }
}
