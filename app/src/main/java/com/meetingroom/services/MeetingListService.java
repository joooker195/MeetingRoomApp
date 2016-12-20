package com.meetingroom.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetingroom.MeetingListActivity;
import com.meetingroom.R;
import com.meetingroom.adapter.GoogleCalendar;
import com.meetingroom.variables.MainVariables;
import com.meetingroom.variables.MeetingRow;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Ксю on 17.12.2016.
 */
public class MeetingListService extends IntentService
{

    Map<String, Map<String, String>> mapMeetings;
    Map<String, String> mapKeys;

    List<MeetingRow> listMeetings = new ArrayList<>();
    ArrayList<String> listKeys = new ArrayList<>();

    MeetingListListener meetingListener;

    private NotificationCompat.Builder mBuilder;

    private Calendar date;
    private int mYear;
    private int mMonth;
    private int mDay;

    public String mDate;
    {
        date = Calendar.getInstance();
        mYear = date.get(Calendar.YEAR);
        mMonth = date.get(Calendar.MONTH);
        mDay = date.get(Calendar.DAY_OF_MONTH);
        mDate = new StringBuilder()
                .append(mDay).append(".")
                .append(mMonth+1).append(".")
                .append(mYear).toString();
    }

    public static final String ACTION_MYINTENTSERVICE = "com.meetingroom.RESPONSE";
    public static final String NETWORK = "NETWORK";
    public static final String MEETINGS = "MEETINGS";

    public MeetingListService() {
        super("MeetingListService");

        Log.v("E_VALUE", "Meeting Service Create");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {

            DatabaseReference a = FirebaseDatabase.getInstance().getReference();
            meetingListener = new MeetingListListener(a);
            a.addValueEventListener(meetingListener);
        }
        else {
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"0");
            responseIntent.putExtra(MEETINGS, (Serializable) listMeetings);
            sendBroadcast(responseIntent);
            stopSelf();
        }

    }

    public class MeetingListListener implements ValueEventListener
    {
        private DatabaseReference mRef;

        public MeetingListListener(DatabaseReference ref)
        {
            this.mRef = ref;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            mapMeetings = (Map<String, Map<String, String>>) dataSnapshot.child("Meetings").getValue();
            mapKeys = (Map<String, String>) dataSnapshot.child("Keys").getValue();

            for (int i = 0; i < mapMeetings.keySet().toArray().length; i++) {
                MeetingRow meeting = new MeetingRow();
                String desc = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("desc");
                String title = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("title");
                String key = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("key");
                String[] date = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("begin").split(" ");
                String[] dateEnd = mapMeetings.get(mapMeetings.keySet().toArray()[i].toString()).get("end").split(" ");
                if (date[0].equals(mDate)) {
                    meeting.setTitle(title);
                    meeting.setDesc(desc);
                    meeting.setKey(key);
                    meeting.setDate(date[0]);
                    meeting.setTimeBegin(date[1]);
                    meeting.setDateEnd(dateEnd[0]);
                    meeting.setTimeEnd(dateEnd[1]);
                    listMeetings.add(meeting);
                }
            }

            for (int i = 0; i < mapKeys.keySet().toArray().length; i++)
            {
                listKeys.add(i, mapKeys.get(mapKeys.keySet().toArray()[i]));
            }

            ifNewMeeting(mRef);

            mRef.removeEventListener(this);

            Intent responseIntent = new Intent();
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK, "1");
            responseIntent.putExtra(MEETINGS, (Serializable) listMeetings);
            sendBroadcast(responseIntent);
            stopSelf();
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {
            Log.e("MLSError:", databaseError.getMessage());

        }
    }

    public  void getNotification(String title)
    {
        Context context = getApplicationContext();
        mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Добавлена новая встреча!")
                        .setContentText(title)
                        .setTicker("Meeting Room!")
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);

        Intent resultIntent = new Intent(context, MeetingListActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MeetingListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());

    }

    //кроме Meetings есть еще один ключ Keys, в котором содержатся ключи существующих встреч.
    //если встреча только что добавлена, то в списке ключей ее еще нет.
    //сравниваем список ключей, со списком встреч, и если есть не совпадение, то отсылаем уведамление и добавляем ключ в список.
    public void ifNewMeeting(DatabaseReference mRef) {

        if (mapKeys.size() < mapMeetings.size())
        {
            for (int i = 0; i < listMeetings.size(); i++)
            {
                boolean f = false;
                MeetingRow meeting = listMeetings.get(i);

                for (int j = 0; j < listKeys.size(); j++) {

                    if (listMeetings.get(i).getKey().equals(listKeys.get(j)))
                    {
                        f = true;
                        break;
                    }
                }

                if (!f) {
                    Context context = getApplicationContext();
                    try {
                        GoogleCalendar.init(context, meeting.getDate(), meeting.getTimeBegin(), meeting.getDateEnd(),
                                meeting.getTimeEnd(), meeting.getTitle(), meeting.getDesc());
                        getNotification(meeting.getTitle());

                        mapKeys.put("k_" + MainVariables.getKey(), meeting.getKey());
                        mRef.child("Keys").setValue(mapKeys);
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        }
    }



}
