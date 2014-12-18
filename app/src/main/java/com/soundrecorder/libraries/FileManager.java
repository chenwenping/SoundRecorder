package com.soundrecorder.libraries;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


public class FileManager {
	
	private String rootFolder;
	private Context c;
	private boolean storageMethod;
	
	public FileManager(Context context)
	{
		c = context;
		storageMethod = false;
		Environment.getExternalStorageDirectory().getPath();
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/SoundRecorder");
		if (file.exists() == false)
		{
			if (file.mkdir())
				Log.e("FileManager", "Ajout du dossier : " + file.getAbsolutePath());
			else
				Log.e("FileManager", "Echec d'ajout dossier : " + file.getAbsolutePath());
		}
		rootFolder = file.getAbsolutePath();
	}
	
	public boolean isStorageMethod()
	{
		return storageMethod;
	}


	public void setStorageMethod(boolean storageMethod)
	{
		this.storageMethod = storageMethod;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && this.isStorageMethod() == true)
		{
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecord");
			if (f.exists() == false)
			{
				File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecord");
				dir.mkdir();
			}
			this.setRootFolder(f.getAbsolutePath());
		}
		else
		{
			this.storageMethod = false;
			File f = c.getDir("SoundRecord", Context.MODE_PRIVATE);
			this.setRootFolder(f.getAbsolutePath());
		}
	}
	
	public String getRootFolder()
	{
		return rootFolder;
	}

	public void setRootFolder(String rootFolder)
	{
		
		this.rootFolder = rootFolder;
	}
	
	public boolean moveFile(String filename, String newPath)
	{
		File file = new File (rootFolder + File.separator + filename);
		
		file.renameTo(new File(rootFolder + File.separator + newPath + File.separator + filename));
		return (true);
	}

	public boolean renameFile(String filename, String newFilename)
	{
		File file = new File (rootFolder + File.separator + filename);
		
		String[] tokens = filename.split("\\.");
		return (file.renameTo(new File(rootFolder + File.separator + newFilename + "." + tokens[tokens.length - 1])));
	}
	
	public boolean removeFile(String filename)
	{
		File file = new File (rootFolder + File.separator + filename);
		
		return (file.delete());
	}
	
	public String[] getContentDir()
	{
		File dir = new File(rootFolder);
		
		if (dir.isDirectory())
			return dir.list();
	    return null;
	}
	
	// TYPES : RingtoneManager.TYPE_NOTIFICATION, RingtoneManager.TYPE_ALARM, RingtoneManager.TYPE_RINGTONE
		public void setAs(int type, String filename) {
			File tmp = new File(rootFolder + File.separator + filename);
			
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, tmp.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, filename);
			if (type == RingtoneManager.TYPE_RINGTONE)
			{
				values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
				values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
				values.put(MediaStore.Audio.Media.IS_ALARM, false);
				values.put(MediaStore.Audio.Media.IS_MUSIC, false);
			}
			else if (type == RingtoneManager.TYPE_ALARM)
			{
				values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
				values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
				values.put(MediaStore.Audio.Media.IS_ALARM, true);
				values.put(MediaStore.Audio.Media.IS_MUSIC, false);
			}
			else if (type == RingtoneManager.TYPE_NOTIFICATION)
			{
				values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
				values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
				values.put(MediaStore.Audio.Media.IS_ALARM, false);
				values.put(MediaStore.Audio.Media.IS_MUSIC, false);
			}
			
			//Insert it into the database
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(tmp.getAbsolutePath());
			Uri newUri = c.getContentResolver().insert(uri, values);

			RingtoneManager.setActualDefaultRingtoneUri(c, type, newUri);
		}
}
