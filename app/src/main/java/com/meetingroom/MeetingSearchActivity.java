package com.meetingroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meetingroom.adapter.RVAdapter;
import com.meetingroom.services.MeetingSearchService;
import com.meetingroom.variables.MeetingRow;

import java.util.ArrayList;

public class MeetingSearchActivity extends AppCompatActivity {


    private RecyclerView mMeetingList;
    ArrayList<MeetingRow> m = new ArrayList<>();

    private EditText mEditText;
    private Button mButtonSearch;

    private String desc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mMeetingList = (RecyclerView) findViewById(R.id.meeting_list_search);
        mMeetingList.setHasFixedSize(true);
        mMeetingList.setLayoutManager(new LinearLayoutManager(this));

        mEditText = (EditText) findViewById(R.id.search_edit);
        mButtonSearch = (Button) findViewById(R.id.search);

        searchMeeting();

        MeetingSearchBroadcastReceiver meetingBroadcast = new MeetingSearchBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                MeetingSearchService.ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(meetingBroadcast, intentFilter);

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desc = mEditText.getText().toString();
                searchMeeting();
                desc = "";
            }
        });
    }

    public class MeetingSearchBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("NETWORK").equals("1")) {
                m = (ArrayList<MeetingRow>) intent.getSerializableExtra(MeetingSearchService.MEETINGS);
                RVAdapter adapter = new RVAdapter(m, MeetingSearchActivity.this);
                mMeetingList.setAdapter(adapter);
            } else Toast.makeText(context, "Network not found!", Toast.LENGTH_LONG).show();

        }
    }

    public void searchMeeting()
    {
        Intent intent = new Intent(MeetingSearchActivity.this, MeetingSearchService.class);
        intent.putExtra(MeetingSearchService.DESC, desc);
        startService(intent);
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
