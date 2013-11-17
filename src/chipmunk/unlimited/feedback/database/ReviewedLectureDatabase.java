package chipmunk.unlimited.feedback.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.webapi.SHA1;

/**
 * The ReviewedLectureDatabase serves one purpose:
 * Given any LectureItem, it must be able to uniquely
 * identify it and know whether or not the local user
 * has reviewed this lecture.
 * 
 * The identification and storing is done by hashing the
 * content of the "toString()" method in LectureItem and storing
 * it.
 */
public class ReviewedLectureDatabase extends DatabaseWrapper {
	private static final String TAG = "ReviewedLectureDatabase";
	
	public static final String TABLE_NAME = "ReviewedLectures";
	public static final String COLUMN_ID  = "_id";
	
	/* The hash of of the reviewed lecture */
	public static final String COLUMN_HASH = "hash";
	
	public static final String TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " 
			+ TABLE_NAME + " ( " 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_HASH + " TEXT NOT NULL UNIQUE "
			+ " );";
	
	
	public ReviewedLectureDatabase(Context context) {
		super(context);
	}
	
	
	public boolean insertLectureItem(LectureItem item) {
		Log.d(TAG, "Tohash: " + item.toString());
		open();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_HASH, getHash(item));
		
		long insertId = mDatabase.insert(TABLE_NAME, null, values);
		close();
		
		Log.d(TAG, "Inserting hash: " + getHash(item));
		
		if (insertId == -1) {
			Log.e(TAG, "Failed to insert: " + item.toString());
			return false;
		}
		
		return true;
	}
	
	public boolean hasUserReviewed(LectureItem item) {
		open();
		long count = DatabaseUtils.queryNumEntries(
						mDatabase, 
						TABLE_NAME, 
						COLUMN_HASH + " = ?",
						new String[] { getHash(item) } );
		
		close();
		return count != 0;
	}
	
	
	private String getHash(LectureItem item) {
		String tohash = item.toString();
		return SHA1.hash(tohash);
	}
}
