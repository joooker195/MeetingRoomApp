package com.meetingroom;

import java.io.Serializable;

/**
 * Created by Ксю on 06.12.2016.
 */
public class MeetingRow implements Serializable {

    private String title;
    private String desc;
    private String key;
    private  String dateEnd;

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    private String timeEnd;

    public String getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(String timeBegin) {
        this.timeBegin = timeBegin;
    }

    private String timeBegin;

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public String getKey() {
        //Класс для создания cardview

        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



    public MeetingRow()
    {

    }

    public MeetingRow(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
