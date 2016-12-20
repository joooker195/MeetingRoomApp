package com.meetingroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.meetingroom.variables.MainVariables;
import com.meetingroom.variables.MeetingPartys;
import com.meetingroom.variables.MeetingRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MeetingDescActivity extends AppCompatActivity {


    private Firebase mRef;
    private Firebase mRefListPartys;
    private MeetingRow m = new MeetingRow();
    private ArrayList<MeetingPartys> mPat = new ArrayList<>();

    public static String KEY = "";

    private TextView mTitle;
    private TextView mDesc;
    private ExpandableListView mPartys;
    private TextView mBegin;
    private TextView mEnd;
    private TextView mPriority;
    private TextView mTextPartys;

    private Button mAddPartyButton;

    private Map<String, Map<String, String>> list  = new HashMap<>();

    //жесть, 4 метода занимают 230 строк...

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
        mPartys = (ExpandableListView) findViewById(R.id.desc_party);
        mTextPartys = (TextView) findViewById(R.id.text_partys);
        mAddPartyButton = (Button) findViewById(R.id.add_party_button);

        meetingDesc();

        MeetingDescBroadcastReceiver meetingBroadcast = new MeetingDescBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                MeetingDescService.ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(meetingBroadcast, intentFilter);

        MeetingPartysBroadcastReceiver meetingPBroadcast = new MeetingPartysBroadcastReceiver();
        IntentFilter intentPFilter = new IntentFilter(
                MeetingDescService.ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(meetingPBroadcast, intentPFilter);



        mAddPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //добавляем участника
                addPartys();
                mAddPartyButton.setVisibility(Button.INVISIBLE);
                //meetingDesc();

            }

        });
    }


    public class MeetingDescBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("NETWORK").equals("1")) {
                m = (MeetingRow) intent.getSerializableExtra(MeetingDescService.MEETING);
                mTitle.setText(m.getTitle());
                mDesc.setText(m.getDesc());
                mBegin.setText("Начало: " + m.getDate());
                mEnd.setText("Конец: " + m.getDateEnd());
                mPriority.setText("Приоритет: " + m.getPriority());

                mPat = (ArrayList<MeetingPartys>) intent.getSerializableExtra(MeetingDescService.PARTYS);
                if(mPat.size()==0)
                {
                    mTextPartys.setText("Участники: \nВ списке пока нет участников");
                }
                else
                {
                    for(int i=0; i<mPat.size(); i++)
                    {//проверка на участие во встече, если участвуешь, убирается кнопка
                        String a = mPat.get(i).getName();
                        //MainVariables связана с настройками,где мы указываем свое имя и должность
                        if(a.equals(MainVariables.getLogin()))
                        {
                            mAddPartyButton.setVisibility(Button.INVISIBLE);
                        }
                    }

                    Map<String, String> mapDataList;
                    Map<String, String> mapChildDataList;

                    ArrayList<Map<String, String>> groupDataList = new ArrayList<>();
                    ArrayList<ArrayList<Map<String, String>>> childDataList = new ArrayList<>();
                    ArrayList<Map<String, String>> childDataItemList;

                    for (int i = 0; i < mPat.size(); i++) {
                        //так как мы не удаляем участников, id можно вести по порядку
                        //здесь получаем мапу, и дальше создаем адаптер
                    //    Map<String, String> childList = mPat.get(i);
                        mapDataList = new HashMap<>();
                        mapDataList.put("number", String.valueOf(i + 1) + ") " + mPat.get(i).getName());

                        groupDataList.add(mapDataList);

                        mapChildDataList = new HashMap<>();
                        childDataItemList = new ArrayList<>();
                        mapChildDataList.put("desc", "Должность: " + mPat.get(i).getProf());
                        childDataItemList.add(mapChildDataList);

                        childDataList.add(childDataItemList);
                    }


                    String groupFrom[] = new String[]{"number"};
                    int groupTo[] = new int[]{android.R.id.text1};

                    String childFrom[] = new String[]{"desc"};
                    int childTo[] = new int[]{android.R.id.text1};

                    SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                            MeetingDescActivity.this, groupDataList,
                            android.R.layout.simple_expandable_list_item_1, groupFrom,
                            groupTo, childDataList, android.R.layout.simple_list_item_1,
                            childFrom, childTo);

                    mPartys.setAdapter(adapter);


                }

            } else Toast.makeText(context, "Network not found!", Toast.LENGTH_LONG).show();

        }
    }

    public class MeetingPartysBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("NETWORK").equals("0"))
             Toast.makeText(context, "Network not found!", Toast.LENGTH_LONG).show();
        }
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_del)
        {
            delMeeting();
            Intent intent = new Intent(MeetingDescActivity.this, MeetingListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void delMeeting()
    {
        try {

            mRefListPartys = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/n_" + KEY);
            mRefListPartys.removeValue();
        }
        catch (Exception e)
        {

        }

    }

    public void meetingDesc()
    {
        Intent intent = new Intent(MeetingDescActivity.this, MeetingDescService.class);
        intent.putExtra(MeetingDescService.KEY, KEY);
        startService(intent);
    }

    public void addPartys()
    {
        Intent intent = new Intent(MeetingDescActivity.this, MeetingPartyService.class);
        intent.putExtra(MeetingPartyService.KEY, KEY);
        intent.putExtra(MeetingPartyService.NAME, MainVariables.getLogin());
        intent.putExtra(MeetingPartyService.PROF, MainVariables.getProf());
        startService(intent);
    }
}
