package com.soundrecorder.activities;

import java.io.File;

import com.soundrecorder.R;
import com.soundrecorder.libraries.FileManager;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FileManagerView extends Activity {
	
	private ImageButton back;
	private LinearLayout svContent;
	private FileManager	fileManager;
	private String[] fileList;
	private int curOptionItem;
	
	private OnClickListener clickListenerBack = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent().setClass(getApplicationContext(), RecorderMenu.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener clickListenerText = new OnClickListener() {
		public void onClick(View v) {
			playSong(v.getId());
		}
	};
	
	private OnLongClickListener clickListenerLongText = new OnLongClickListener() {
		public boolean onLongClick(View arg0) {
			curOptionItem = arg0.getId();
			return false;
		}
	};
	
	private void deleteClick()
	{
		if (fileManager.removeFile(fileList[curOptionItem]) == true)
    	{
    		svContent.removeAllViews();
    		loadFileList();
    		Toast.makeText(getBaseContext(), "File has been removed", Toast.LENGTH_LONG).show();
    	}
    	else
    		Toast.makeText(getBaseContext(), "Error while removing the file. Try again !", Toast.LENGTH_LONG).show();
	}
	
	private void renameClick()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("File name");
		alert.setMessage("Enter the new file name");

		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  Editable value = input.getText();
			if (fileManager.renameFile(fileList[curOptionItem], value.toString()) == true)
	    	{
	    		svContent.removeAllViews();
	    		loadFileList();
	    		Toast.makeText(getBaseContext(), "File has been renamed", Toast.LENGTH_LONG).show();
	    	}
	    	else
	    		Toast.makeText(getBaseContext(), "Error while renaming the file. Try again !", Toast.LENGTH_LONG).show();
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    return ;
		  }
		});

		alert.show();
	}
	
	private void loadFileList()
	{
		int i;
		
		fileList = fileManager.getContentDir();
		for (i = 0; i < fileList.length; i++)
		{
			TextView text = new TextView(this);
			text.setTextColor(Color.WHITE);
			if (i % 2 != 0)
				text.setBackgroundColor(Color.rgb(90, 90, 90));
			text.setPadding(0, 30, 0, 30);
			text.setOnClickListener(clickListenerText);
			text.setOnLongClickListener(clickListenerLongText);
			text.setText(fileList[i]);
			text.setId(i);
			svContent.addView(text);
		}
		if (i == 0)
		{
			TextView text = new TextView(this);
			text.setTextColor(Color.WHITE);
			text.setText("No sound recorded");
			svContent.addView(text);
		}
	}
	
	private void setAsRingtone()
	{
		fileManager.setAs(RingtoneManager.TYPE_RINGTONE, fileList[curOptionItem]);
	}

	private void setAsAlarm()
	{
		fileManager.setAs(RingtoneManager.TYPE_ALARM, fileList[curOptionItem]);
	}
	
	private void setAsNotif()
	{
		fileManager.setAs(RingtoneManager.TYPE_NOTIFICATION, fileList[curOptionItem]);
	}
	
	private void playSong(int song)
	{
		Intent intent = new Intent().setClass(getApplicationContext(), SoundPlayerView.class);
		intent.putExtra("songId", song);
		startActivity(intent);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.sv_content)
		{
		  super.onCreateContextMenu(menu, v, menuInfo);
		  menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Play");
		  menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "Rename");
		  menu.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, "Delete");
		  menu.add(Menu.NONE, Menu.FIRST + 3, Menu.NONE, "Share");
		  menu.add(Menu.NONE, Menu.FIRST + 4, Menu.NONE, "Set as Ringtone");
		  menu.add(Menu.NONE, Menu.FIRST + 5, Menu.NONE, "Set as Alarm");
		  menu.add(Menu.NONE, Menu.FIRST + 6, Menu.NONE, "Set as Notification");
	    }
	  }
	
	public void shareClick()
	{
		
		try
		{
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("audio/3gpp");
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
			File file =  new File( fileManager.getRootFolder() + File.separator + fileList[curOptionItem]);
			sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			startActivity(Intent.createChooser(sharingIntent, "Share SoundRecorder Application"));
		}
		catch (Exception e)
		{
			Log.i("exception", "event share catch e = " + e);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case Menu.FIRST:
	    		playSong(curOptionItem);
	    		break;
	        
	    	case Menu.FIRST + 1:
	        	renameClick();
	        	break;
	        	
	        case Menu.FIRST + 2:
	        	deleteClick();
	        	break;
	        	
	        case Menu.FIRST + 3:
	        	shareClick();
	        	break;
	        
	        case Menu.FIRST + 4:
	        	setAsRingtone();
	        	break;
		        
		    case Menu.FIRST + 5:
		        setAsAlarm();
		        break;
			        
		    case Menu.FIRST + 6:
		        setAsNotif();
		        break;
	    }
	    return super.onContextItemSelected(item);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager_view);
        
        back = (ImageButton) findViewById(R.id.back_btn);
        svContent = (LinearLayout) findViewById(R.id.sv_content);
        
        back.setOnClickListener(clickListenerBack);
        fileManager = new FileManager(this.getApplicationContext());
        registerForContextMenu(svContent);
        loadFileList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_file_manager_view, menu);
        return true;
    }
}
