package com.meetingroom.adapter;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Ксю on 14.12.2016.
 */
public class GoogleCalendar {

    private static String calName;

    public static final String[] EVENT_PROJECTION = new String[] {"_id", "name",};

    public static void init(Context context, String beginDate, String beginTime, String endDate,
                            String endTime, String title, String desc) throws ParseException
    {
        String calId = null;

        ContentResolver cr = context.getContentResolver();
        Uri calendars = CalendarContract.Calendars.CONTENT_URI;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = cr.query(calendars, EVENT_PROJECTION, null, null, null);
        ContentValues event = new ContentValues();


        long start = parseDate(beginDate+" "+beginTime);
        long end = parseDate(endDate+" "+endTime);

        if (managedCursor.moveToFirst()) {

            int nameColumn = managedCursor.getColumnIndex("name");
            int idColumn = managedCursor.getColumnIndex("_id");

            do{
                calName = managedCursor.getString(nameColumn);
                calId = managedCursor.getString(idColumn);

                event.put("calendar_id", calId);
                event.put("title", title);
                event.put("description", desc);
                event.put("dtstart", start);
                event.put("dtend", end);
                event.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC/GMT +3:00");

                Uri eventsUri = Uri.parse("content://com.android.calendar/events");
                context.getContentResolver().insert(eventsUri, event);
            }
            while (managedCursor.moveToNext());
        }
    }

    private static long parseDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return simpleDateFormat.parse(date).getTime();


    }


}
