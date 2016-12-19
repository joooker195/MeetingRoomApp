package com.meetingroom.variables;

import java.io.Serializable;

/**
 * Created by Ксю on 19.12.2016.
 */
public class MeetingPartys implements Serializable
{
    private String name;
    private String prof;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

  /*  public MeetingPartys(String name, String prof, int count) {
        this.name = name;
        this.prof = prof;
        this.count = count;
    }*/

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
