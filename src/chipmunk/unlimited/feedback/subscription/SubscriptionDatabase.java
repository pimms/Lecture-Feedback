package chipmunk.unlimited.feedback.subscription;

import java.util.ArrayList;
import java.util.List;

import chipmunk.unlimited.feedback.DatabaseWrapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @class SubscriptionDatabase
 * Class providing an interface to the subscription
 * table in the local database. 
 */
public class SubscriptionDatabase extends DatabaseWrapper {
	private static final String TAG = "SubscriptionHelper";
	
	/* The name of the table */
	public static final String TABLE_NAME 	= "Subscription";
	
	/* Integral unique ID {PK} */
	public static final String COLUMN_ID 	= "_id";
	
	/* TimeEdit's unique identification of the object */
	public static final String COLUMN_TE_CODE  = "timeedit_code";
	
	/* HiG's unique identification of the object */
	public static final String COLUMN_HIG_CODE = "hig_code";
	
	/* The actual name of the course or class */
	public static final String COLUMN_NAME  = "name";
	
	/* All columns */
	public static final String[] ALL_COLUMNS = {
		COLUMN_ID, COLUMN_TE_CODE, COLUMN_HIG_CODE, COLUMN_NAME
	};
	
	public static final String TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + " ( "
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_TE_CODE + " VARCHAR(16) NOT NULL, "
			+ COLUMN_HIG_CODE + " VARCHAR(16) NOT NULL, "
			+ COLUMN_NAME + " TEXT NOT NULL "
			+ ");";
	
	
	public SubscriptionDatabase(Context context) {
		super(context);
	}

	
	public Cursor getSubscriptionCursor() {
		return mDatabase.query(TABLE_NAME, ALL_COLUMNS, 
							null, null, null, null, null);
	}
	
	public List<SubscriptionItem> getSubscriptionList() {
		open();
		
		List<SubscriptionItem> list = new ArrayList<SubscriptionItem>();
		Cursor cursor = getSubscriptionCursor();
		cursor.moveToFirst();
		
		int teCodeIndex = cursor.getColumnIndex(COLUMN_TE_CODE);
		int higCodeIndex = cursor.getColumnIndex(COLUMN_HIG_CODE);
		int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
		
		while (!cursor.isAfterLast()) {
			String teCode = cursor.getString(teCodeIndex);
			String higCode = cursor.getString(higCodeIndex);
			String name = cursor.getString(nameIndex);
			cursor.moveToNext();
		
			SubscriptionItem item = new SubscriptionItem(teCode, higCode, name);
			list.add(item);
		}
		
		cursor.close();
		close();
		
		return list;
	}
	
	
	public void addSubscription(String timeEditCode, String higCode, String name) {
		open();
		
		Log.d(TAG, "Adding sub: " + timeEditCode + ", " + higCode + ", " + name);
		
		final ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, name);
		values.put(COLUMN_TE_CODE, timeEditCode);
		values.put(COLUMN_HIG_CODE, higCode);
		
		long insertId = mDatabase.insert(TABLE_NAME, null, values);
		mDatabase.close();
		
		if (insertId == -1) {
			Log.e(TAG, "Failed to insert '" + name + "' into DB");
		}
	}
	
	public void deleteSubscription(int id) {
		mDatabase.delete(TABLE_NAME, COLUMN_ID+"="+id, null);
	}
}

















