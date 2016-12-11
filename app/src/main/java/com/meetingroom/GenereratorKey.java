package com.meetingroom;

/**
 * Created by Ксю on 11.12.2016.
 */
public class GenereratorKey {

    private static int key = 50;

    public static String getKey()
    {
        key++;
        return String.valueOf(key);

    }
}
