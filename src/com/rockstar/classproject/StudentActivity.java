package com.rockstar.classproject;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.rockstar.classproject.util.GenericDatabase;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class StudentActivity extends SherlockActivity {
TextView name,gender,lang;
String fname,sname;
String tname,tgender,tlang,tsname;
long id;
Context dialogContext;
GenericDatabase gd=new GenericDatabase(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student);
		 dialogContext = new ContextThemeWrapper(this,R.style.Holo_Theme);
		//get bundle
		Bundle bun=getIntent().getExtras();
//		fname=bun.getString("fname");
//		sname=bun.getString("sname");
		id=bun.getLong("key");
		name=(TextView)findViewById(R.id.tv_name);
		gender=(TextView)findViewById(R.id.tv_gender);
		lang=(TextView)findViewById(R.id.tv_lang);
		
		gd.open();
		 try {
			 Cursor c=gd.getContact(id+1);
			
			if (c.moveToFirst())     
				DisplayContact(c);
				//  Toast.makeText(getApplicationContext(), c.getString(1), Toast.LENGTH_LONG).show();
	        else
	            Toast.makeText(this, "No student found", Toast.LENGTH_LONG).show();
	      
			gd.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 
		
	}
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.details, menu);
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.del:
		
		//	Toast.makeText(getApplicationContext(), "jsj", Toast.LENGTH_LONG).show();
			Delete(id);
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	  public void DisplayContact(Cursor c) {
		  name.setText(c.getString(1)+" "+c.getString(2));
		  gender.setText(c.getString(3));
		  lang.setText(c.getString(4));
		  

		}
	  public void Delete(long id)
	  {
		  gd.open();
	        if (gd.DeleteContact(id))
	        {
	        	AlertDialog alert = new AlertDialog.Builder(dialogContext).create();
			alert.setTitle("Success");
			alert.setMessage("Contact Has been Deleted");
			alert.setButton(Dialog.BUTTON_POSITIVE,
					getResources().getString(R.string.dl_cont),
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int which) {
							Intent intent = new Intent(getApplicationContext(),MainActivity.class);
							startActivity(intent);
						}
					});
			alert.setButton(Dialog.BUTTON_NEGATIVE,
					getResources().getString(R.string.dl_exit),
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int which) {
							finish();
						}
					});

			alert.show();
	        }
	        else
	            Toast.makeText(this, "Delete failed.", Toast.LENGTH_LONG).show();
	        gd.close();
	  }
	
}
