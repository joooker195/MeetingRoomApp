package com.meetingroom;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Ксю on 07.12.2016.
 */
public class MeetingApp extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
