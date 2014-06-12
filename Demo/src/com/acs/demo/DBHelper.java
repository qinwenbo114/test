package com.acs.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "demo_sys.db";//�������ݿ���
	public static final String USER_TABLE_NAME = "demo_table";//�������
	public static final String USER_TABLE_NAME_FIELD1 = "user_id";//��������1����
	public static final String USER_TABLE_NAME_FIELD2 = "user_name";//��������2
	public static final String USER_TABLE_NAME_FIELD3 = "password";//��������3
	public static final String USER_TABLE_NAME_FIELD4 = "rem";//��������4
	public static final String USER_TABLE_NAME_FIELD5 = "url";//��������5
	
	public static final int DATABASE_VERSION = 3;
	
	private static final String TAG = "DBHelper";

	public DBHelper(Context context) {//���캯��
		super(context, DATABASE_NAME, null, DATABASE_VERSION);//super()���Լ̳и���
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + USER_TABLE_NAME + " ("
				//����һ����Ϊsql��SQL����Է����Ժ�ִ��ʱ����
				+ "_id integer PRIMARY KEY autoincrement," 
				+  USER_TABLE_NAME_FIELD1 + ","
				+  USER_TABLE_NAME_FIELD2 + ","
				+  USER_TABLE_NAME_FIELD3 + ","
				+  USER_TABLE_NAME_FIELD4 + ","
				+  USER_TABLE_NAME_FIELD5
				+ ")";
		try {
			db.execSQL(sql);//����ִ��SQL���
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());//�����ִ������׳��쳣
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);//ִ��SQL���drop table if exists UESR_TABLE_NAME
		// Create a new one.
		onCreate(db);
	}
	
	public void close(SQLiteDatabase db) {   
	     if (db != null) {   
	     db.close();   
	     }   
	 } 

}
