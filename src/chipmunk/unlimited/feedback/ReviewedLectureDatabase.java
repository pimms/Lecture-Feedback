package chipmunk.unlimited.feedback;

import chipmunk.unlimited.feedback.webapi.SHA1;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
	public static final String TABLE_NAME = "ReviewedLectures";
	public static final String COLUMN_ID  = "_id";
	
	/* The hash of of the reviewed lecture */
	public static final String COLUMN_HASH = "hash";
	
	private static final String TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " 
			+ TABLE_NAME + " ( " 
			+ COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_HASH + " TEXT NOT NULL UNIQUE "
			+ " );";
	
	
	public ReviewedLectureDatabase(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}
	
	
	public void insertLectureItem(LectureItem item) {
		
	}
	
	
	private String getHash(LectureItem item) {
		return SHA1.hash(item.toString());
	}
}
