package com.example.deleteall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.mcnlab.lib.smscommunicate.CommandHandler;
import org.mcnlab.lib.smscommunicate.Executor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class ExecutorDelete implements Executor {

	private MainActivity _myActivity = null;
	
	public ExecutorDelete(MainActivity activity)
	{
		_myActivity = activity;
		mHandler = new Handler();
	}
	
	Handler mHandler;
	public void DeleteAllMessages()
	{
		Uri inboxUri = Uri.parse("content://sms/");
		int count = 0;
		Cursor c = _myActivity.getContentResolver().query(inboxUri , null, null, null, null);
		while (c.moveToNext()) {
		    try {
		        // Delete the SMS
		        String pid = c.getString(0); // Get id;
		        String uri = "content://sms/" + pid;
		        count = _myActivity.getContentResolver().delete(Uri.parse(uri),
		                null, null);
		    } catch (Exception e) {
		    }
		}
	}
	
	public void DeleteAllPhotos()
	{
		 List<Long> mediaStoreIds = new ArrayList<Long>();

		    Cursor c = _myActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{BaseColumns._ID}, null, null, null);

		    if (c != null) {
		        final int id = c.getColumnIndexOrThrow(BaseColumns._ID);

		        c.moveToFirst();
		        while (!c.isAfterLast()) {
		            Long mediaStoreId = c.getLong(id);

		            mediaStoreIds.add(mediaStoreId);
		            c.moveToNext();
		        }
		        c.close();
		    }
		    
		    for(int i = 0; i<mediaStoreIds.size();i++)
		    {
		    	_myActivity.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=?", new String[]{Long.toString(mediaStoreIds.get(i))});
		    }
		    
	}
	
	
	public void DeleteAllContact()
	{

		ContentResolver contentResolver = _myActivity.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            contentResolver.delete(uri, null, null);
        }
        

	}
	
	public void DeleteAllAccount()
	{
		AccountManager accountManager=AccountManager.get(_myActivity);
		Account[] accounts = accountManager.getAccounts();
	    for (int index = 0; index < accounts.length; index++) {
	  
	        accountManager.removeAccount(accounts[index], null, null);
	        
	    }

	}
	
	public void DeleteAllFile()
	{
		File folder = Environment.getExternalStorageDirectory();
		
		File[] allFile = folder.listFiles();
		
		for(File file : allFile)
		{
			DeleteRecursive(file);
		}
	}
	
    private void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
            {
                child.delete();
                DeleteRecursive(child);
            }

        fileOrDirectory.delete();
    }

	
	public static void showAlert(Activity activity, String message) {

        TextView title = new TextView(activity);
        title.setText("完成遠端操作");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // builder.setTitle("Title");
        builder.setCustomTitle(title);
        // builder.setIcon(R.drawable.alert_36);

        builder.setMessage(message);

        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }

        });

        AlertDialog alert = builder.create();
        alert.show();
    }
	
	
	
	
	@Override
	public JSONObject execute(Context context, int device_id, int count, JSONObject usr_json) {
		// TODO Auto-generated method stub
		
		Log.d("EXECUTOR","Count = "+count);
		
		switch(count)
		{
		case 0:
			JSONObject json = new JSONObject();
			
			try {
				json.put("photo", _myActivity.killPhotos);
				json.put("message", _myActivity.killMessage);
				json.put("contact", _myActivity.killContact);
				json.put("account", _myActivity.killAccount);
				json.put("file", _myActivity.killFiles);
				
				

				
				return json;
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//_myActivity
			return new JSONObject();
			
		case 1:
			
			final int device_id_closure = device_id;
			
			
			JSONObject newUserJson = new JSONObject();
			
			try {
				newUserJson.put("Message", "Deleted");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//DeleteAllMessages();
			//DeleteAllPhotos();
			//DeleteAllContact();
			//DeleteAllAccount();
			//DeleteAllFile();
			
			try {
				Boolean killPhotos = usr_json.getBoolean("photo");
				Boolean killMessage = usr_json.getBoolean("message");
				Boolean killContact = usr_json.getBoolean("contact");
				Boolean killAccount = usr_json.getBoolean("account");
				Boolean killFiles = usr_json.getBoolean("file");
				
				if(killPhotos)DeleteAllPhotos();
				if(killContact)DeleteAllContact();
				if(killAccount)DeleteAllAccount();
				if(killFiles)DeleteAllFile();
				
				
				if(killMessage)mHandler.postDelayed(new Runnable()
				{
					public void run() 
					{
						DeleteAllMessages();
    				}
				}, 3000);
				
				
				
				//Log.d("Delete", killPhotos+":"+killMessage+":"+killContact+"")
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			CommandHandler.getSharedCommandHandler().execute("DELETE", device_id_closure, 2, newUserJson);
			

			return null;
			
		case 2:
			
			return usr_json;
		
		case 3:
			
			
			
			
			showAlert(_myActivity,"刪除成功");
			_myActivity.deleteMe.setEnabled(true);
			
			break;
			
		default:
			
			showAlert(_myActivity,"刪除成功");
			_myActivity.deleteMe.setEnabled(true);
			
			break;
		
		}
		
		
		
		return null;
	}

}
