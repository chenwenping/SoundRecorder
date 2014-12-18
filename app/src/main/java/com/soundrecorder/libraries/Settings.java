package com.soundrecorder.libraries;

import com.soundrecorder.ressources.FileFormats;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
		
	private Context context = null;
	private boolean notif = false;
	
	public boolean getNotif()
	{
		return notif;
	}
	
	public Settings(final Context context) {
        this.context = context;
    }
	
	public String getFilePrefix() {
	   SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
	   return sharedPref.getString("editPrefix", "Record_");
   }
	
	public boolean getAutoRecordCall() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context); 
		String tmp = sharedPref.getString("listAutorecord", "3");
		int tmp2 = Integer.parseInt(tmp);
		if (tmp2 == 1)
		{
			notif = false;
			return true;
		}
		else if (tmp2 == 2)
		{	
			notif = true;
			return true;
		}
		else if (tmp2 == 3)
		{
			notif = false;
			return false;
		}
		return false;
	}
	
	public boolean getStereo() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context); 
		return sharedPref.getBoolean("checkboxStereo", true);
	}
	
	public int getBitRates() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context); 
		String tmp = sharedPref.getString("listBitrates", "64000");
		return Integer.parseInt(tmp);
	}
	
	public FileFormats getFormat() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context); 
		String tmp = sharedPref.getString("listFormats", "0");
		int tmp2 = Integer.parseInt(tmp);
		if (tmp2 == 1)
			return FileFormats.GPP;
		else if (tmp2 == 2)
			return FileFormats.MP4;
		else if (tmp2 == 3)
			return FileFormats.AMR;
		else
			return FileFormats.GPP;
	}
}

