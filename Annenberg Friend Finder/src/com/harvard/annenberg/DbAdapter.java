package com.harvard.annenberg;

import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DbAdapter {

	public static final String USER_DB_TABLE = "person";

	public static final String KEY_USER_ID = "local_id";
	public static final int KEY_USER_ID_NUM = 0;
	public static final String KEY_USER_NAME = "name";
	public static final int KEY_USER_NAME_NUM = 1;
	public static final String KEY_USER_IMAGE = "image";
	public static final int KEY_USER_LNAME_NUM = 2;
	public static final String KEY_USER_HUID = "huid";
	public static final int KEY_USER_HUID_NUM = 3;
	public static final String KEY_SERVER_ID = "server_id";
	public static final int KEY_SERVER_ID_NUM = 4;
	public static final String FLAG_1 = "flag1";
	public static final int KEY_FLAG_1 = 5;
	public static final String FLAG_2 = "flag2";
	public static final int KEY_FLAG_2 = 6;
	public static final String FLAG_3 = "flag3";
	public static final int KEY_FLAG_3 = 7;
	public static final String FLAG_4 = "flag4";
	public static final int KEY_FLAG_4 = 8;
	public static final String FLAG_5 = "flag5";
	public static final int KEY_FLAG_5 = 9;

	private static final String DATABASE_NAME = "user";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper mDbHelper;
	public SQLiteDatabase mDb;

	private final Context context;

	public DbAdapter(Context _context) {
		this.context = _context;
		mDbHelper = null;
		mDb = null;
	}

	/**
	 * Table creation sql statement
	 */
	private static final String USER_DB_CREATE = "create table "
			+ USER_DB_TABLE + " (" + KEY_USER_ID
			+ " integer primary key autoincrement, " + KEY_USER_NAME
			+ " text default 'Unknown', " + KEY_USER_IMAGE
			+ " text default 'Unknown', " + KEY_USER_HUID
			+ " integer default '0', " + KEY_SERVER_ID + " long, " + FLAG_1
			+ " text default 'Unknown', " + FLAG_2
			+ " text default 'Unknown', " + FLAG_3
			+ " text default 'Unknown', " + FLAG_4
			+ " text default 'Unknown', " + FLAG_5 + " text default 'Unknown'"
			+ ");";

	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(USER_DB_CREATE);
			Log.v("Database", USER_DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	/**
	 * Open the contacts database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @param readOnly
	 *            if the database should be opened read only
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public void open(boolean readOnly) throws SQLException {
		if (mDbHelper == null) {
			mDbHelper = new DatabaseHelper(context);
			if (readOnly) {
				mDb = mDbHelper.getReadableDatabase();
			} else {
				mDb = mDbHelper.getWritableDatabase();
			}
		}

	}

	/**
	 * Close the database
	 */
	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
			mDbHelper = null;
		}
	}

	public int createContact(String name, String imageUri, int HUID) {

		open(true);
		String image = "";
		if (!imageUri.equals(""))
			image = imageUri;

		ContentValues vals = new ContentValues();
		// vals.put(KEY_CONTACT_ID, contactId);
		vals.put(KEY_USER_NAME, name.trim());
		vals.put(KEY_USER_IMAGE, image.trim());
		vals.put(KEY_USER_HUID, HUID);
		return (int) mDb.insert(USER_DB_TABLE, null, vals);
	}

	public int updateContact(int id, String name, String imageUri, int HUID,
			long serverId) {
		open(true);

		String image = "";
		if (!imageUri.equals(""))
			image = imageUri;

		ContentValues vals = new ContentValues();
		// vals.put(KEY_CONTACT_ID, contactId);
		vals.put(KEY_USER_NAME, name.trim());
		vals.put(KEY_USER_IMAGE, image.trim());
		vals.put(KEY_USER_HUID, HUID);
		vals.put(KEY_SERVER_ID, serverId);

		return mDb.update(USER_DB_TABLE, vals, KEY_USER_ID + "=" + id,
				null);

	}

	public Cursor fetchUser(int id) throws SQLException {
		boolean found = false;
		open(true);
		Cursor mCursor = mDb.query(true,USER_DB_TABLE, null,
				KEY_USER_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			found = mCursor.moveToFirst();
		}
		if (!found) {
			if (mCursor != null) {
				mCursor.close();
			}
			return null;
		}
		return mCursor;
	}

	public Cursor fetchUser(long id) throws SQLException {
		boolean found = false;
		open(true);
		Cursor mCursor = mDb.query(true, USER_DB_TABLE, null, KEY_SERVER_ID
				+ "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			found = mCursor.moveToFirst();
		}
		if (!found) {
			if (mCursor != null) {
				mCursor.close();
			}
			return null;
		}
		return mCursor;
	}

	/**
	 * Fetch a list of all contacts in the database
	 * 
	 * @return Db cursor
	 */
	public Cursor fetchAllUsers() {
		boolean found = false;
		open(true);
		Cursor mCursor = mDb.query(USER_DB_TABLE, null, null, null, null,
				null, null);

		if (mCursor != null) {
			found = mCursor.moveToFirst();
		}
		if (!found) {
			if (mCursor != null) {
				mCursor.close();
			}
			return null;
		}
		return mCursor;
	}

	public int updateServerId(int contactId, long server_id) {
		open(true);

		ContentValues vals = new ContentValues();
		vals.put(KEY_SERVER_ID, server_id);
		return mDb.update(USER_DB_TABLE, vals, KEY_USER_ID + "="
				+ contactId, null);
	}
}
