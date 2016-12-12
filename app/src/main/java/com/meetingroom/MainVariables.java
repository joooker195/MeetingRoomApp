package com.meetingroom;

import java.util.Random;

/**
 * Created by Ксю on 11.12.2016.
 */
public class MainVariables {

    private static int key = 50;
    private static String prof = "No";
    private static String login = "Meeting Room";

    private static Random randomKey = new Random();

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        MainVariables.login = login;
    }

    public static String getProf() {
        return prof;
    }

    public static void setProf(String prof) {
        MainVariables.prof = prof;
    }

    public static String getKey()
    {
        key = randomKey.nextInt(10000);
        return String.valueOf(key);
    }

}
