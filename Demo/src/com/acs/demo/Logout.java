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
			SQLiteOpenHelper dbhepler = new DBHelper(context);// 建立一个DBHelper对象dbhelper
			SQLiteDatabase db = dbhepler.getReadableDatabase();// 建立一个SQLiteDatabase对象db，获取一个可以进行读操作数据库对象
			String sql = "update " + DBHelper.USER_TABLE_NAME
					+ " set rem='0' where rem='1'";
			db.execSQL(sql);// 执行SQL语句
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

}
