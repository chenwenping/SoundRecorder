package com.soundrecorder.activities;

import java.util.Calendar;

import com.soundrecorder.R;
import com.soundrecorder.libraries.AudioManager;
import com.soundrecorder.libraries.FileManager;
import com.soundrecorder.libraries.Settings;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RecorderMenu extends Activity {

	private Button recordButton;
	private Button playerButton;
	private Button managerButton;
	private TelephonyManager tm;
	private AudioManager audio = new AudioManager();
	private Settings setting = new Settings(this);
	private String filename = null;
	private FileManager fileManager = null;

	private OnClickListener clickListenerRecord = new OnClickListener() {
		public void onClick(View v) {		
			Intent intent = new Intent().setClass(getApplicationContext(), RecordView.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener clickListenerPlayer = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent().setClass(getApplicationContext(), SoundPlayerView.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener clickListenerManage = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent().setClass(getApplicationContext(), FileManagerView.class);
			startActivity(intent);
		}
	};

	private PhoneStateListener psl = new PhoneStateListener()
    {
    	public void onCallStateChanged(int state, String incomingNumber)
    	{
    		try
    		{
    			switch (state)
    			{
    			case TelephonyManager.CALL_STATE_OFFHOOK:
    				if (setting.getAutoRecordCall())
    				{
    					SharedPreferences myPrefs;   
    					myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);  
    					boolean RecordNotif = myPrefs.getBoolean("RECORDNOTIF", false);
    					Calendar c = Calendar.getInstance();
						
						filename = setting.getFilePrefix() + (String.format("%04d", (Integer)c.get(Calendar.YEAR))) + '-';
						filename += (String.format("%02d", (Integer)c.get(Calendar.MONTH))) + '-';
						filename += (String.format("%02d", (Integer)c.get(Calendar.DAY_OF_MONTH))) + ' ';
						filename += (String.format("%02d", (Integer)c.get(Calendar.HOUR))) + '.';
						filename += (String.format("%02d", (Integer)c.get(Calendar.MINUTE))) + '.';
						filename += (String.format("%02d", (Integer)c.get(Calendar.SECOND)));

    					if (audio.isRecording() == false)
    					{
    						if (RecordNotif || setting.getNotif() == false)
    						{
    							audio.recordCall(filename, setting.getBitRates(), setting.getFormat());
    							Toast.makeText(RecorderMenu.this, "Record start", Toast.LENGTH_SHORT).show();
    						}
    					}
    				}
    				break;
    			case TelephonyManager.CALL_STATE_RINGING:
    				if (setting.getAutoRecordCall())
    				{
    					if (setting.getNotif())
    					{
    						SystemClock.sleep(1000);
    					    Intent intent = new Intent().setClass(getApplicationContext(), PopUp.class);
    					    startActivity(intent);
    					}
    				}
    				break;
    			case TelephonyManager.CALL_STATE_IDLE:
    				if (audio.isRecording())
    				{
    					SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
    					SharedPreferences.Editor prefsEditor;  
    					prefsEditor = myPrefs.edit();  
    					prefsEditor.remove("RECORDNOTIF");
    					prefsEditor.commit();
    					audio.stopRecording();
    					audio = new AudioManager();
    					audio.setRootFolder(fileManager.getRootFolder());
    					Toast.makeText(getBaseContext(), "File recorded has been saved as " + filename, Toast.LENGTH_LONG).show();
    				}			
    				break;
    				default:
    					Toast.makeText(getBaseContext(), "Switch default ", Toast.LENGTH_LONG).show();
    				
    			}
    			
    		}
    		catch (Exception e)
    		{
    			Log.i("exception", "event call catch e = " + e);
    		}
    	}
    	
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder_menu);
        recordButton = (Button) findViewById(R.id.record_button);
        playerButton = (Button) findViewById(R.id.sound_player);
        managerButton = (Button) findViewById(R.id.file_manager);
        
        recordButton.setOnClickListener(clickListenerRecord);
        playerButton.setOnClickListener(clickListenerPlayer);
        managerButton.setOnClickListener(clickListenerManage);
        
        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        tm.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);
        FileManager fileManager = new FileManager(this.getApplicationContext());
        audio.setRootFolder(fileManager.getRootFolder());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_recorder_menu, menu);
        return true;
    }
    
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
