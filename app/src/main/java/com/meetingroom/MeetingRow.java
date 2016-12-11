package com.meetingroom;

/**
 * Created by Ксю on 06.12.2016.
 */
public class MeetingRow {

    private String title;
    private String desc;
    private String key;

    public String getKey() {
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
