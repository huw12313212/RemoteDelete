package com.example.deleteall;

import org.mcnlab.lib.smscommunicate.CommandHandler;
import org.mcnlab.lib.smscommunicate.Recorder;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	public final static String LOG_TAG = "DeleteALL";
	
	public boolean killPhotos = false;
	public boolean killMessage = false;
	public boolean killContact = false;
	public boolean killAccount = false;
	public boolean killFiles = false;
	public Button deleteMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //UserDefined.filter = "$FINDME$";
        
        Recorder.init(this, LOG_TAG);
        CommandHandler.init(this);
        
        CommandHandler.getSharedCommandHandler().addExecutor("DELETE", new ExecutorDelete(this));
        
        
        deleteMe = (Button) findViewById(R.id.button1);
        
        final Activity thisActivity = this;
        
        deleteMe.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				killPhotos = ( (CheckBox) findViewById(R.id.checkBox1)).isChecked();
				killMessage = ( (CheckBox) findViewById(R.id.CheckBox01)).isChecked();
				killContact = ( (CheckBox) findViewById(R.id.CheckBox02)).isChecked();
				killAccount = ( (CheckBox) findViewById(R.id.CheckBox03)).isChecked();
				killFiles = ( (CheckBox) findViewById(R.id.CheckBox04)).isChecked();
				
				
				String phonenumber = ((EditText) findViewById(R.id.editText1)).getText().toString();
				
				Recorder rec = Recorder.getSharedRecorder();
				
				CommandHandler hdlr = CommandHandler.getSharedCommandHandler();
				SQLiteDatabase db = rec.getWritableDatabase();
				int device_id = rec.getDeviceIdByPhonenumberOrCreate(db, phonenumber);
				db.close();
				hdlr.execute("DELETE", device_id, 0, null);
				deleteMe.setEnabled(false);


			}
        	
        });
        
        
    
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
