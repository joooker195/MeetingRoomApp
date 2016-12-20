package com.meetingroom.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.firebase.client.Firebase;

/**
 * Created by Ксю on 19.12.2016.
 */
public class MeetingDelService extends IntentService {
    public MeetingDelService() {
        super("MeetingDelService");
    }

    String key = "";

    public static final String ACTION_MYINTENTSERVICE = "com.meetingroomDel.RESPONSE";
    public static final String NETWORK = "NETWORK";
    public static final String KEY = "KEY";

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Intent responseIntent = new Intent();

        key = intent.getStringExtra(KEY);

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {


            Firebase mRefListMeeting = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/n_" + key);
            mRefListMeeting.removeValue();

            Firebase mRefListKey = new Firebase("https://meeting-room-3a41e.firebaseio.com/Keys/k_" + key);
            mRefListKey.removeValue();

            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"1");
            sendBroadcast(responseIntent);
            stopSelf();

        }
        else {
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"0");
            sendBroadcast(responseIntent);
            stopSelf();
        }

    }
}
