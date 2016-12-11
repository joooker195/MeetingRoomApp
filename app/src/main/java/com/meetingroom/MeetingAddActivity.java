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

import com.firebase.client.Firebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MeetingAddActivity extends AppCompatActivity {

    private Button mAddMeeting;
    private Firebase mRef;
    private String key = "";

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

        key = GenereratorKey.getKey();

        mRef = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/");

        mAddMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Firebase mChildRef = mRef.child("n_" + key);

                    boolean isEnptyFlag = (mEditTitle.getText().toString().equals("") || mEditDesc.getText().toString().equals("")
                            || mEditDateBegin.getText().toString().equals("") || mEditTimeBegin.getText().toString().equals("")
                            || mEditDateEnd.getText().toString().equals("")  || mEditTimeEnd.getText().toString().equals("")
                            || mEditPriority.getText().toString().equals(""));

                    boolean parseDateFlag = (isValidDate(mEditDateBegin.getText().toString(), "dd.MM.yyyy") &&
                                                isValidDate(mEditDateEnd.getText().toString(), "dd.MM.yyyy"));

                    boolean parseTimeFlag = (isValidTime(mEditTimeBegin.getText().toString(), "HH:mm") &&
                            isValidTime(mEditTimeEnd.getText().toString(), "HH:mm"));

                    if (isEnptyFlag || !parseDateFlag || !parseTimeFlag)
                    {
                        throw new Exception();
                    }

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

                    Firebase mChildRefKey = mChildRef.child("key");
                    mChildRefKey.setValue(key);

                    startActivity(new Intent(MeetingAddActivity.this, MeetingListActivity.class));
                }
                catch (Exception e)
                {
                    Toast.makeText(MeetingAddActivity.this, "Проверьте правильность заполнения полей", Toast.LENGTH_LONG).show();
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


    static boolean isValidDate(String value, String datePattern) {

        if (value == null || datePattern == null || datePattern.length() <= 0) {
            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    static boolean isValidTime(String value, String datePattern) {

        if (value == null || datePattern == null || datePattern.length() <= 0) {
            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
