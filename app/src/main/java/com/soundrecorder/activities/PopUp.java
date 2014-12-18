package com.soundrecorder.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.soundrecorder.R;

public class PopUp extends Activity {
	
	Context c = null;
	
	private OnClickListener clickListenerYesRecord = new OnClickListener() {
		public void onClick(View v) {
			SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
			SharedPreferences.Editor prefsEditor;  
			prefsEditor = myPrefs.edit();
			prefsEditor.putBoolean("RECORDNOTIF", true);  
			prefsEditor.commit();
			finish();
		}
	};

	private OnClickListener clickListenerNoRecord = new OnClickListener() {
		public void onClick(View v) {
			SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
			SharedPreferences.Editor prefsEditor;  
			prefsEditor = myPrefs.edit();  
			prefsEditor.putBoolean("RECORDNOTIF", false);  
			prefsEditor.commit();
			finish();
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        Button bYes = (Button)findViewById(R.id.recordYES);
        bYes.setOnClickListener(clickListenerYesRecord);
        Button bNo = (Button)findViewById(R.id.recordNO);
        bNo.setOnClickListener(clickListenerNoRecord);
        c = this;
    }

}
