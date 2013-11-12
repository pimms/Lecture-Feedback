package chipmunk.unlimited.feedback.subscription;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @class SubscriptionHelper
 * Class providing an interface to the subscription
 * table in the local database. 
 */
public class SubscriptionDBHelper extends SQLiteOpenHelper {
	private static final String TAG = "SubscriptionHelper";
	
	/* The name of the table */
	public static final String TABLE_NAME 	= "Subscription";
	
	/* Integral unique ID {PK} */
	public static final String COLUMN_ID 	= "_id";
	
	/* TimeEdit's unique identification of the object */
	public static final String COLUMN_CODE  = "code";
	
	/* The actual name of the course or class */
	public static final String COLUMN_NAME  = "name";
	
	/* All columns */
	public static final String[] ALL_COLUMNS = {
		COLUMN_ID, COLUMN_CODE, COLUMN_NAME
	};
	
	private static final String DB_NAME = "LectureReview";
	private static final int DB_VERSION = 1;
	
	private static final String TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + " ( "
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_CODE + " VARCHAR(16) NOT NULL, "
			+ COLUMN_NAME + " TEXT NOT NULL "
			+ ");";

	private SQLiteDatabase mDatabase;
	
	
	public SubscriptionDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	
	public void open() {
		if (mDatabase == null) {
			mDatabase = getWritableDatabase();
		}
	}
	
	public void close() {
		mDatabase.close();
		mDatabase = null;
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
		
		int codeIndex = cursor.getColumnIndex(COLUMN_CODE);
		int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
		
		while (!cursor.isAfterLast()) {
			String code = cursor.getString(codeIndex);
			String name = cursor.getString(nameIndex);
			cursor.moveToNext();
		
			SubscriptionItem item = new SubscriptionItem(code, name);
			list.add(item);
		}
		
		cursor.close();
		close();
		
		return list;
	}
	
	
	public void addSubscription(String code, String name) {
		open();
		
		final ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, name);
		values.put(COLUMN_CODE, code);
		
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

















