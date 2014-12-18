package com.soundrecorder.activities;

import java.util.Calendar;

import com.soundrecorder.R;
import com.soundrecorder.libraries.AudioManager;
import com.soundrecorder.libraries.FileManager;
//import com.soundrecorder.libraries.FileManager;
import com.soundrecorder.libraries.Settings;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RecordView extends Activity {
	
	private ImageButton back;
	private ImageButton mic;
	private TextView	textInfo;
	private TextView	timeInfo;
	private Boolean 	isRecording = false;
	private Boolean		isOut = true;
	private Integer		sec, min, hour;
	private String		filename = null;
	private Settings	settings = new Settings(this);
	private AudioManager audioManager = new AudioManager();
	private FileManager	fileManager;
	
	private String getFileName()
	{
		String filename = null;
		
		Calendar c = Calendar.getInstance(); 
		
		filename = settings.getFilePrefix() + (String.format("%04d", (Integer)c.get(Calendar.YEAR))) + '-';
		filename += (String.format("%02d", (Integer)c.get(Calendar.MONTH))) + '-';
		filename += (String.format("%02d", (Integer)c.get(Calendar.DAY_OF_MONTH))) + ' ';
		filename += (String.format("%02d", (Integer)c.get(Calendar.HOUR))) + '.';
		filename += (String.format("%02d", (Integer)c.get(Calendar.MINUTE))) + '.';
		filename += (String.format("%02d", (Integer)c.get(Calendar.SECOND)));
		return (filename);
	}
	
	private void updateTime() {
	    new Thread() {
	        @Override
	        public void run() {
				while (isRecording == true)
				{
					isOut = false;
					runOnUiThread(new Runnable() {
                        public void run() {
                        	timeInfo.setText(String.format("%02d", hour) + ':' + String.format("%02d", min) + ':' + String.format("%02d", sec));
                        	sec++;
                        	if (sec == 60)
                        	{
                        		min++;
                        		sec = 0;
                        	}
                        	if(min == 60)
                        	{
                        		hour++;
                        		min = 0;
                        	}
                        }
                 });
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				isOut = true;
	        }
	 }.start();

	}
	
	private OnClickListener clickListenerBack = new OnClickListener() {
		public void onClick(View v) {
			if (isRecording == true)
				Toast.makeText(getBaseContext(), "Stop record before exiting", Toast.LENGTH_LONG).show();
			else
			{
			Intent intent = new Intent().setClass(getApplicationContext(), RecorderMenu.class);
			startActivity(intent);
			}
		}
	};
	
	private OnClickListener clickListenerMic = new OnClickListener() {
		public void onClick(View v) {
			if (isRecording == false)
			{
				if (isOut == true)
				{
					audioManager.setStereo(settings.getStereo());
					mic.setImageResource(R.drawable.mic_recording);
					isRecording = true;
					textInfo.setText("Recording ...");
					filename = getFileName();
					audioManager.recordMic(filename, settings.getBitRates(), settings.getFormat());
					updateTime();
				}
			}
			else
			{
				if (sec > 2)
				{
					mic.setImageResource(R.drawable.mic);
					audioManager.stopRecording();
					isRecording = false;
					textInfo.setText("Press mic to start recording");
					sec = 0;
					min = 0;
					hour = 0;
					timeInfo.setText("00:00:00");
					Toast.makeText(getBaseContext(), "File recorded has been saved as " + filename, Toast.LENGTH_LONG).show();
					filename = null;
				}
			}
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_view);
        
        back = (ImageButton) findViewById(R.id.back_btn);
        mic = (ImageButton) findViewById(R.id.mic_image);
        textInfo = (TextView) findViewById(R.id.record_view_info);
        timeInfo = (TextView) findViewById(R.id.record_time);
        
        back.setOnClickListener(clickListenerBack);
        mic.setOnClickListener(clickListenerMic);
        textInfo.setText("Press mic to start recording");
        timeInfo.setText("00:00:00");
        fileManager = new FileManager(this.getApplicationContext());
        audioManager.setRootFolder(fileManager.getRootFolder());
        
		sec = 0;
		min = 0;
		hour = 0;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_recorder_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_settings:
            Intent intent = new Intent(this, SettingsView.class);
            this.startActivity(intent);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
