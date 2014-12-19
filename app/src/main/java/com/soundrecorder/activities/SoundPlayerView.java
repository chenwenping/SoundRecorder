package com.soundrecorder.activities;

import com.soundrecorder.R;
import com.soundrecorder.libraries.AudioManager;
import com.soundrecorder.libraries.FileManager;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SoundPlayerView extends Activity {
	private ImageButton back, prevButton, nextButton, playButton;
	private TextView	songTitle, curTime, totalTime, noSong;
	private LinearLayout song_ll;

	private FileManager	fileManager;
	private AudioManager audioManager = new AudioManager();
	private boolean toFileManager = false;
	private int curSong;
	private String[] fileList;
	
	private OnClickListener clickListenerBack = new OnClickListener() {
		public void onClick(View v) {
			audioManager.stopSong();
			if (toFileManager == true)
			{
				Intent intent = new Intent().setClass(getApplicationContext(), FileManagerView.class);
				startActivity(intent);
			}
			else
			{
				Intent intent = new Intent().setClass(getApplicationContext(), RecorderMenu.class);
				startActivity(intent);
			}
		}
	};
	
	private OnClickListener clickListenerPrev = new OnClickListener() {
		public void onClick(View v) {
			boolean wasPlaying;
			
			wasPlaying = audioManager.isPlaying();
			audioManager.stopSong();
			curTime.setText("00:00:00");
			if (curSong > 0)
				curSong--;
			else
				curSong = fileList.length - 1;
			audioManager.loadSong(fileManager.getRootFolder() + '/' + fileList[curSong]);
			songTitle.setText(fileList[curSong]);
			totalTime.setText(getDuration());
			if (wasPlaying == true)
				audioManager.playSong();
		}
	};
	
	private OnClickListener clickListenerNext = new OnClickListener() {
		public void onClick(View v) {
			boolean wasPlaying;
			
			wasPlaying = audioManager.isPlaying();
			audioManager.stopSong();
			curTime.setText("00:00:00");
			if (curSong < fileList.length - 1)
				curSong++;
			else
				curSong = 0;
			audioManager.loadSong(fileManager.getRootFolder() + '/' + fileList[curSong]);
			songTitle.setText(fileList[curSong]);
			totalTime.setText(getDuration());
			if (wasPlaying == true)
				audioManager.playSong();
		}
	};
	
	
	private OnClickListener clickListenerPlay = new OnClickListener() {
		public void onClick(View v) {
			playSong();
		}
	};
	
	private void playSong()
	{
		if (audioManager.isPlaying() == false)
		{
			audioManager.playSong();
			playButton.setImageResource(R.drawable.pause);
		}
		else
		{
			audioManager.pauseSong();
			playButton.setImageResource(R.drawable.play);
		}
		totalTime.setText(getDuration());
		updateTime();
	}
	
	private void updateTime() {
	    new Thread() {
	        @Override
	        public void run() {
				while (audioManager.isPlaying() == true)
				{
					if (audioManager.isPaused() == false)
					{
						runOnUiThread(new Runnable() {
							public void run() {
								if (getCurPos().compareTo(getDuration()) == 0)
								{
									playSong();
									curTime.setText("00:00:00");
									audioManager.setCurrentTime(0);
								}
								else
									curTime.setText(getCurPos());
							}
						});
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
	        }
	 }.start();

	}
	
	private String getCurPos()
	{
		Integer hour;
		Integer min;
		int base;
		
		base = audioManager.getCurrentTime() / 1000;
		min = base / 60;
		hour = min / 60;
		min -= hour * 60;
		
		return String.format("%02d", hour) + ':' + String.format("%02d", min) + ':' + String.format("%02d", base % 60);
	}
	
	private String getDuration()
	{
		Integer hour;
		Integer min;
		int base;
		
		base = audioManager.getDuration() / 1000;
		min = base / 60;
		hour = min / 60;
		min -= hour * 60;
		
		return String.format("%02d", hour) + ':' + String.format("%02d", min) + ':' + String.format("%02d", base % 60);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_player_view);
        
        back = (ImageButton) findViewById(R.id.back_btn);
        back.setOnClickListener(clickListenerBack);
        prevButton = (ImageButton) findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(clickListenerPrev);
        nextButton = (ImageButton) findViewById(R.id.next_btn);
        nextButton.setOnClickListener(clickListenerNext);
        playButton = (ImageButton) findViewById(R.id.play_pause_btn);
        playButton.setOnClickListener(clickListenerPlay);

        song_ll = (LinearLayout) findViewById(R.id.song_ll);
        songTitle = (TextView) findViewById(R.id.song_title);
        curTime = (TextView) findViewById(R.id.cur_time);
        totalTime = (TextView) findViewById(R.id.total_time);
        noSong = (TextView) findViewById(R.id.no_song);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
			curSong = extras.getInt("songId");
		else
        	curSong = 0;
        fileManager = new FileManager(this.getApplicationContext());
        fileList = fileManager.getContentDir();
        if (fileList.length == 0)
        {
            noSong.setVisibility(View.VISIBLE);
            song_ll.setVisibility(View.GONE);
            noSong.setText("No sound recorded");
        } else {
            noSong.setVisibility(View.GONE);
            song_ll.setVisibility(View.VISIBLE);
            songTitle.setText(fileList[curSong]);
            audioManager.loadSong(fileManager.getRootFolder() + '/' + fileList[curSong]);
            totalTime.setText(getDuration());
            if (extras != null)
            {
                toFileManager = true;
                playSong();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sound_player_view, menu);
        return true;
    }
}
