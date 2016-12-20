package com.meetingroom.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.firebase.client.Firebase;
import com.meetingroom.variables.MainVariables;

/**
 * Created by Ксю on 20.12.2016.
 */
public class MeetingAddService extends IntentService
{
    public MeetingAddService() {
        super("MeetingAddService");
    }

    String title = "";
    String desc = "";
    String begin = "";
    String end = "";
    String priority = "";

    public static final String ACTION_MYINTENTSERVICE = "com.meetingroomAdd.RESPONSE";
    public static final String NETWORK = "NETWORK";
    public static final String TITLE = "TITLE";
    public static final String DESC = "DESC";
    public static final String BEGIN = "BEGIN";
    public static final String END = "END";
    public static final String PRIORITY = "PRIORITY";


    @Override
    protected void onHandleIntent(Intent intent)
    {
        Intent responseIntent = new Intent();

        title = intent.getStringExtra(TITLE);
        desc = intent.getStringExtra(DESC);
        begin = intent.getStringExtra(BEGIN);
        end = intent.getStringExtra(END);
        priority = intent.getStringExtra(PRIORITY);

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {

            String newKey = MainVariables.getKey();

            Firebase mRefMeet = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/n_"+newKey);
            mRefMeet.child("title").setValue(title);
            mRefMeet.child("desc").setValue(desc);
            mRefMeet.child("begin").setValue(begin);
            mRefMeet.child("end").setValue(end);
            mRefMeet.child("key").setValue(newKey);
            mRefMeet.child("priority").setValue(priority);


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
