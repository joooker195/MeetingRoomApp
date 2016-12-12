package com.meetingroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MeetingDescActivity extends AppCompatActivity {


    private Firebase mRef;
    private Firebase mRefListPartys;

    public static String KEY = "";

    Map<String, Map<String, String>> delmap = new HashMap<>();

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

        //список встреч
        mRef = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/");
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //метод для получения список встреч в реальном времени
                try {
                    //получаем мапу, и по ключу(тому самому, что в при вызове интента мы получили через одно место), находим
                    // нужный нам список)
                    Map<String, Map<String, String>> map = dataSnapshot.getValue(Map.class);
                    mTitle.setText(map.get("n_" + KEY).get("title").toString());
                    mDesc.setText(map.get("n_" + KEY).get("desc").toString());
                    mBegin.setText("Начало: " + map.get("n_" + KEY).get("begin").toString());
                    mEnd.setText("Конец: " + map.get("n_" + KEY).get("end").toString());
                    mPriority.setText("Приоритет: " + map.get("n_" + KEY).get("priority").toString());

                }
                catch (Exception e)
                {

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        //список участников(отдельно, так как это подсписок)
        mRefListPartys = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/n_" + KEY + "/partys");
        mRefListPartys.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    mTextPartys.setText("Участники:");

                    list = dataSnapshot.getValue(Map.class);

                    for(int i=0; i<list.size(); i++)
                    {//проверка на участие во встече, если участвуешь, убирается кнопка
                        String a = dataSnapshot.child("p_"+i).child("name").getValue(String.class);
                        //MainVariables связана с настройками,где мы указываем свое имя и должность
                        if(a.equals(MainVariables.getLogin()))
                        {
                            mAddPartyButton.setVisibility(Button.INVISIBLE);
                        }
                    }


                    //вся ахинея, что написана ниже, нужна для отображения списка, так как я использовала ExpandableListView,
                    //чтобы выводился участник, и при разворачивании его подсписка отображалась его должность
                    Map<String, String> mapDataList;
                    Map<String, String> mapChildDataList;

                    ArrayList<Map<String, String>> groupDataList = new ArrayList<>();
                    ArrayList<ArrayList<Map<String, String>>> childDataList = new ArrayList<>();
                    ArrayList<Map<String, String>> childDataItemList;

                    for (int i = 0; i < list.size(); i++) {
                        //так как мы не удаляем участников, id можно вести по порядку
                        //здесь получаем мапу, и дальше создаем адаптер
                        Map<String, String> childList = list.get("p_" + i);
                        mapDataList = new HashMap<>();
                        mapDataList.put("number", String.valueOf(i + 1) + ") " + childList.get("name"));

                        groupDataList.add(mapDataList);

                        mapChildDataList = new HashMap<>();
                        childDataItemList = new ArrayList<>();
                        mapChildDataList.put("desc", "Должность: " + childList.get("prof"));
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

                catch (Exception e)
                {
                    //если списка с участниками нет, то падает ошибка, что не находтся ключ partys.
                    //обработала ее и вывела сообщение
                    mTextPartys.setText("Участники: \nВ списке пока нет участников");
                    //без инициализации, которая вот тут происходит, почему то падает NPE (не знаю почему)
                    list = new HashMap<>();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        mAddPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //добавляем участника
                Firebase mChildRefPartys = mRefListPartys.child("p_" + list.size()).child("name");
                mChildRefPartys.setValue(MainVariables.getLogin());
                mChildRefPartys = mRefListPartys.child("p_" + list.size()).child("prof");
                mChildRefPartys.setValue(MainVariables.getProf());
                mAddPartyButton.setVisibility(Button.INVISIBLE);

            }

        });
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
            startActivity(intent);
        }
        if (id == R.id.action_del)
        {
            delMeeting();
            Intent intent = new Intent(MeetingDescActivity.this, MeetingListActivity.class);
            startActivity(intent);
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
}
