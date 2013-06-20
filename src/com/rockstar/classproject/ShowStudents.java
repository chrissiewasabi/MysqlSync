package com.rockstar.classproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

import com.actionbarsherlock.app.SherlockActivity;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.rockstar.classproject.util.CheckConnectivity;
import com.rockstar.classproject.util.GenericDatabase;




import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShowStudents extends SherlockActivity {
	GenericDatabase gd = new GenericDatabase(this);
	SimpleCursorAdapter cursoradapter;
	ListView studentlist;
	String fname,sname,gender,language;
	 Boolean isInternetPresent = false;
	 Context dialogContext;
	 // Connection detector class
	    CheckConnectivity cd;
	    HttpClient httpclient;
		HttpPost httpPost;
		String result,feedback;
		AlertDialog notification;
		HttpResponse response;
		ProgressDialog syncing;
		Handler handle;
		StringBuilder sb;
		InputStream inputstream;
		TextView f,s;
	   
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_students);
		studentlist=(ListView)findViewById(R.id.listview);
		 cd = new CheckConnectivity(getApplicationContext());
		 isInternetPresent = cd.isConnectingToInternet();
		 dialogContext = new ContextThemeWrapper(this,R.style.Holo_Theme);
		 
				
		gd.open();
		
		 Cursor c=gd.getAllContacts(); 
				cursoradapter=new SimpleCursorAdapter(this,
						R.layout.list_row,c,
						new String[]{gd.KEY_NAME,gd.KEY_SNAME,gd.KEY_GENDER,gd.KEY_LANG},
						new int[]{R.id.title,R.id.title2,R.id.duration,R.id.artist}
						);	
				studentlist.setAdapter(cursoradapter);
			 
	
			
			 gd.close();
			 
		OnItemClickListener listen=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				 f=(TextView)view.findViewById(R.id.title);
				 s=(TextView)view.findViewById(R.id.title2);
				 	String fname=f.getText().toString();
				 	String sname=s.getText().toString();
				 	long id=arg2;
				Bundle pack=new Bundle();
				pack.putString("fname", fname);
				pack.putString("sname", sname);
				pack.putLong("key",id);
			startActivity(new Intent(getApplicationContext(),StudentActivity.class).putExtras(pack));
			}
		};	
		studentlist.setOnItemClickListener(listen);	 
	}
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_show_students, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.sync:
		SyncToRemote();
		//	Toast.makeText(getApplicationContext(), "jsj", Toast.LENGTH_LONG).show();
			
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	@SuppressLint("HandlerLeak")
	public void SyncToRemote()
	{
		//this method will sync sqlite to mysql as long as internet is present
		if(!isInternetPresent)
		{	
			AlertDialog alert = new AlertDialog.Builder(dialogContext).create();
		alert.setTitle("Incomplete");
		alert.setMessage("No Connectivity");
		alert.setButton(Dialog.BUTTON_POSITIVE,
				getResources().getString(R.string.dl_cont),
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int which) {
						Intent intent = new Intent(
								Settings.ACTION_WIRELESS_SETTINGS);
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
			
		}//end if
		else
		{
			
			syncing= ProgressDialog.show(dialogContext, "", "Sync in Progress...", true,false);
			final String url="http://itgrowth.net/developer/christine/async/sync.php";
			Thread save = new Thread() {
				public void run() {
					 Looper.prepare();
					gd.open();
					 Cursor cursor=gd.getAllContacts(); 
					 @SuppressWarnings("unchecked")
					
					int count=cursor.getCount();
					while(cursor.moveToNext())
					{
						fname=cursor.getString(1);
						sname=cursor.getString(2);
						gender=cursor.getString(3);
						language=cursor.getString(2);
					}
					 try
					 {
						 HttpClient httpclient = new DefaultHttpClient();
						 httpPost=new HttpPost(url);
						 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
						
							 nameValuePairs.add(new BasicNameValuePair("fname", fname));
							 nameValuePairs.add(new BasicNameValuePair("sname", sname));
							 nameValuePairs.add(new BasicNameValuePair("gender", gender));
							 nameValuePairs.add(new BasicNameValuePair("lang", language));
							 httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
								response=httpclient.execute(httpPost);
								HttpEntity entity = response.getEntity();
								inputstream = entity.getContent();
								ResponseHandler<String> responceHandler = new BasicResponseHandler();
								String response = httpclient.execute(httpPost, responceHandler);
						 
					 }
					 catch(IOException e)
					 {
						 
					 }
					 try
					 {
						 BufferedReader reader = new BufferedReader(
									new InputStreamReader(inputstream,
											"iso-8859-1"), 8);
							sb = new StringBuilder(); 
							String line = null;
							while ((line = reader.readLine()) != null) {
								sb.append(line + "\n");
							}
							inputstream.close();
							result = sb.toString();
							Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
							handle.sendEmptyMessage(0);
							
					 }
					 catch(UnsupportedEncodingException e)
						{
							
						} catch (IOException e) {
							
							e.printStackTrace();
						}
					
					handle.sendEmptyMessage(0);
				}// end runnable	
				
			};//end thread
			 
			save.start();
			handle=new Handler()
			{
				@Override
				public void handleMessage(Message msg) {
					 if(result.charAt(0)=='s')
					    {
						 
						syncing.dismiss();
							
						Toast.makeText(getApplicationContext(), "Uploaded!", Toast.LENGTH_LONG).show();
					    }
					 syncing.dismiss();
				}
				
			};//end handler
			Looper.loop();
			
		}//end else
		
	}//end Sync method
	
	
	

}
