package com.rockstar.classproject;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.rockstar.classproject.util.GenericDatabase;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * @author wasabi
 * 
 */
public class MainActivity extends SherlockActivity implements OnClickListener {
	private EditText fname;
	private EditText lname;
	private RadioGroup rg_gender;
	private CheckBox cb_c, cb_j, cb_i;
	private ProgressDialog saving;
	private String name, sname, gender, language, java, ios, c;
	int radio_id;
	Handler handler;
	GenericDatabase gd;
	Context dialogContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gd = new GenericDatabase(this);
		 dialogContext = new ContextThemeWrapper(this,R.style.Holo_Theme);
		fname = (EditText) findViewById(R.id.et_fname);
		lname = (EditText) findViewById(R.id.et_lname);
		rg_gender = (RadioGroup) findViewById(R.id.rd_gender);
		cb_c = (CheckBox) findViewById(R.id.cb_c);
		cb_j = (CheckBox) findViewById(R.id.cb_java);
		cb_i = (CheckBox) findViewById(R.id.cb_ios);

		rg_gender.setOnClickListener(this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.save:
			SubmitValues();
		//	Toast.makeText(getApplicationContext(), "jsj", Toast.LENGTH_LONG).show();
			
			break;
		case android.R.id.home:
			startActivity(new Intent(getApplicationContext(),ShowStudents.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("HandlerLeak")
	public void SubmitValues() {
		
		name = fname.getText().toString();
		sname = lname.getText().toString();
		radio_id = rg_gender.getCheckedRadioButtonId();
		language = "";
		if (radio_id == R.id.rd_male) {
			gender = "male";
		}
		if (radio_id == R.id.rd_female) {
			gender = "female";
		}
		// check for null fields
		if (name == ""|| sname == ""
				|| radio_id == -1
				|| (cb_c.isChecked() == false && cb_i.isChecked() == false && cb_j
						.isChecked() == false)) {
			AlertDialog alert = new AlertDialog.Builder(dialogContext).create();
			alert.setTitle("Incomplete");
			alert.setMessage("One or more Fields are empty");
			alert.setButton(Dialog.BUTTON_POSITIVE,
					getResources().getString(R.string.dl_cont),
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int which) {

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
			// get the values

		}// end if
		else
		{
			if (cb_c.isChecked() == true) {
				c = cb_c.getText().toString();
				language = language + c + ",";
			}
			if (cb_i.isChecked() == true) {
				ios = cb_c.getText().toString();
				language = language + ios + ",";
			}
			if (cb_j.isChecked() == true) {
				java = cb_j.getText().toString();
				language = language + java + ",";
			}
			// save values to the sqlitedatabase....start thread
		//	saving = ProgressDialog.show(dialogContext, "", "saving...", true,false);
			Thread save = new Thread() {
				public void run() {
					
					gd.open();

					long id = gd.insertContact(name, sname, gender, language);
					gd.close();
					handler.sendEmptyMessage(0);
				}// end runnable
			};// end thread
			save.start();
handler=new Handler()
{

	@Override
	public void handleMessage(Message msg) {
		//saving.dismiss();
		AlertDialog alert = new AlertDialog.Builder(dialogContext).create();
		alert.setMessage(name+"'s details saved!");
		alert.setButton(Dialog.BUTTON_POSITIVE,
				getResources().getString(R.string.dl_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int which) {
						startActivity(new Intent(getApplicationContext(),ShowStudents.class));
					}
				});
		alert.show();
		
	}
	
};//end handler()
	}
	}//end method

}
