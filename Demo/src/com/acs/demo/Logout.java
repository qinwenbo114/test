package com.acs.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Logout
{
	public boolean Lgot(Context context)
	{
		try
		{
			SQLiteOpenHelper dbhepler = new DBHelper(context);// ����һ��DBHelper����dbhelper
			SQLiteDatabase db = dbhepler.getReadableDatabase();// ����һ��SQLiteDatabase����db����ȡһ�����Խ��ж��������ݿ����
			String sql = "update " + DBHelper.USER_TABLE_NAME
					+ " set rem='0' where rem='1'";
			db.execSQL(sql);// ִ��SQL���
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

}
