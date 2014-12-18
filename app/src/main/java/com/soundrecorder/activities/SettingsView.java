package com.soundrecorder.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.soundrecorder.R;

public class SettingsView extends PreferenceActivity {
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.activity_settings_view);
    }   
}
