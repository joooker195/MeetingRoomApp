package com.meetingroom;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by Ксю on 11.12.2016.
 */
public class SettingPreference  extends PreferenceActivity implements Preference.OnPreferenceChangeListener
{
    Preference mEditName;
    Preference mEditProf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_activity);

        mEditName = findPreference("editName");
        mEditProf = findPreference("editProf");

        mEditName.setSummary(MainVariables.getLogin());
        mEditProf.setSummary(MainVariables.getProf());

        mEditProf.setOnPreferenceChangeListener(this);
        mEditName.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o)
    {
        if(preference.equals(mEditName))
        {
            MainVariables.setLogin(o.toString());
            preference.setSummary(o.toString());
        }
        if(preference.equals(mEditProf))
        {
            MainVariables.setProf(o.toString());
            preference.setSummary(o.toString());
        }
        return true;
    }
}
