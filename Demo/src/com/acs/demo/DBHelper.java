package com.acs.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "demo_sys.db";//定义数据库名
	public static final String USER_TABLE_NAME = "demo_table";//定义表名
	public static final String USER_TABLE_NAME_FIELD1 = "user_id";//定义列名1（）
	public static final String USER_TABLE_NAME_FIELD2 = "user_name";//定义列名2
	public static final String USER_TABLE_NAME_FIELD3 = "password";//定义列名3
	public static final String USER_TABLE_NAME_FIELD4 = "rem";//定义列名4
	public static final String USER_TABLE_NAME_FIELD5 = "url";//定义列名5
	
	public static final int DATABASE_VERSION = 3;
	
	private static final String TAG = "DBHelper";

	public DBHelper(Context context) {//构造函数
		super(context, DATABASE_NAME, null, DATABASE_VERSION);//super()用以继承父类
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + USER_TABLE_NAME + " ("
				//定义一条名为sql的SQL语句以方便以后执行时调用
				+ "_id integer PRIMARY KEY autoincrement," 
				+  USER_TABLE_NAME_FIELD1 + ","
				+  USER_TABLE_NAME_FIELD2 + ","
				+  USER_TABLE_NAME_FIELD3 + ","
				+  USER_TABLE_NAME_FIELD4 + ","
				+  USER_TABLE_NAME_FIELD5
				+ ")";
		try {
			db.execSQL(sql);//尝试执行SQL语句
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());//若出现错误则抛出异常
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);//执行SQL语句drop table if exists UESR_TABLE_NAME
		// Create a new one.
		onCreate(db);
	}
	
	public void close(SQLiteDatabase db) {   
	     if (db != null) {   
	     db.close();   
	     }   
	 } 

}
