package com.rockstar.classproject.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class GenericDatabase {
	public static final String KEY_ROWID="_id";
	public static final String KEY_NAME="name";
	//public static final String KEY_EMAIL="email";
	public static final String KEY_SNAME="sname";
	public static final String KEY_LANG="language";
	public static final String KEY_GENDER="gender";
	public static final String TAG="DBAdapter";

	public static final String DATABASE_NAME="wasabi";
	public static final String DATABASE_TABLE="students";
	public static final int DATABASE_VERSION=1;

	public static final String DATABASE_CREATE="create table students(_id integer primary key autoincrement," +
			"name text not null,sname text not null,gender text not null,language text not null);";
	private  final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	public GenericDatabase(Context ctx){
		this.context=ctx;
		DBHelper=new DatabaseHelper(context);
	}
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		public DatabaseHelper(Context context) {
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		try
		{
			db.execSQL(DATABASE_CREATE);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG,"Upgrading dtatabase from version"+oldVersion+" to "+newVersion+",which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS contacts");
			onCreate(db);
		}
	}//end inner class
	//open db
	public GenericDatabase open() throws SQLException
	{
		db=DBHelper.getWritableDatabase();
		return this;
		
	}
	//close db
	public void close()
	{
		DBHelper.close();
	}
	//insert
	public long insertContact(String name,String sname,String gender,String language)
	{
		ContentValues initialValues=new ContentValues();
		initialValues.put(KEY_NAME,name);
		initialValues.put(KEY_SNAME,sname);
		initialValues.put(KEY_GENDER,gender);
		initialValues.put(KEY_LANG,language);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	//delete
	public boolean DeleteContact(long rowid)
	{
		return db.delete(DATABASE_TABLE, KEY_ROWID+"="+rowid, null)>0;
	}
	//retrieve all
	public Cursor getAllContacts()
	{
		Cursor all= db.query(DATABASE_TABLE, new String[]{KEY_ROWID,KEY_NAME,KEY_SNAME,KEY_GENDER,KEY_LANG}, null, null, null, null, null);
		if (all != null) {
			   all.moveToFirst();
			  }
			  return all;
			 
	}
	//retrive specific
	public Cursor getContact(long id)throws Exception
	{
		Cursor mcursor=
				db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,KEY_NAME,KEY_SNAME,KEY_GENDER,KEY_LANG},KEY_ROWID+"="+id,null, null, null, null, null);
		if(mcursor!=null)
		{
			mcursor.moveToFirst();
		}
		return mcursor;
	}
	public Cursor getStudent(String fname)
	{
		Cursor mcursor=db.query(true, DATABASE_TABLE,  new String[]{KEY_ROWID,KEY_NAME,KEY_SNAME,KEY_GENDER,KEY_LANG}, KEY_NAME+"="+fname , null, null, null, null, null);
		if(mcursor!=null)
		{
			mcursor.moveToFirst();
		}
		return mcursor;
	}
	
	
	//update
	public boolean updateContact(long rowid,String name,String sname,String gender,String language)
	{
		ContentValues args=new ContentValues();
		args.put(KEY_NAME,name);
		args.put(KEY_SNAME,sname);
		args.put(KEY_GENDER,gender);
		args.put(KEY_LANG,language);
		return db.update(DATABASE_TABLE, args, KEY_ROWID+"="+rowid, null)>0;
	}
	}


