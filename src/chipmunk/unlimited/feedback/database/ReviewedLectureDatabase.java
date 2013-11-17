package chipmunk.unlimited.feedback.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;
import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;

/**
 * The ReviewedLectureDatabase serves one purpose:
 * Given any LectureItem, it must be able to uniquely
 * identify it and know whether or not the local user
 * has reviewed this lecture and store the values the user
 * gave the lecture.
 * 
 * The identification and storing is done by hashing the
 * content of the "toString()" method in LectureItem and storing
 * it.
 * 
 * The data is stringified to some extent, and is saved on the form:
 * 	+---------------+-----------------+----------------------------+
 *  | HASH 			| RATINGS 		  | COMMENTS 				   |
 *  +---------------+-----------------+----------------------------+
 *  | <SHA1-val>	| 1.0.0.1.1 	  | Great lecture, proffy 	   |
 *  +---------------+-----------------+----------------------------+
 */
public class ReviewedLectureDatabase extends DatabaseWrapper {
	private static final String TAG = "ReviewedLectureDatabase";
	
	public static final String TABLE_NAME = "ReviewedLectures";
	public static final String COLUMN_ID  = "_id";
	public static final String COLUMN_RATING = "ratings";
	public static final String COLUMN_COMMENT = "comment";
	
	/* The hash of of the reviewed lecture */
	public static final String COLUMN_HASH = "hash";
	
	public static final String TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " 
			+ TABLE_NAME + " ( " 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_HASH + " TEXT NOT NULL UNIQUE, "
			+ COLUMN_RATING + " TEXT NOT NULL, "
			+ COLUMN_COMMENT + " TEXT "
			+ " );";
	
	
	public ReviewedLectureDatabase(Context context) {
		super(context);
	}
	
	/**
	 * Insert a reviewed lecture item into the database.
	 * 
	 * @param item
	 * The lecture item rated by the user.
	 * 
	 * @param ratings
	 * Boolean array containing the ratings.
	 * 
	 * @param comment
	 * The comment made by the user.
	 * 
	 * @return
	 * True if the insertion succeeded, false otherwise.
	 */
	public boolean insertLectureItem(LectureReviewItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_HASH, item.getLectureHash());
		values.put(COLUMN_RATING, item.getRatingString());
		values.put(COLUMN_COMMENT, item.getComment());
		
		open();
		long insertId = mDatabase.insert(TABLE_NAME, null, values);
		close();
		
		Log.d(TAG, "Inserting hash: " + item.getLectureHash());
		
		if (insertId == -1) {
			Log.e(TAG, "Failed to insert: " + item.toString());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Check if the user has review the lecture.
	 * 
	 * @param item
	 * LectureItem the user MAY have reviewed.
	 * 
	 * @return
	 * true if the user has reviewed, false otherwise.
	 */
	public boolean hasUserReviewed(LectureItem item) {
		open();
		long count = DatabaseUtils.queryNumEntries(
						mDatabase, 
						TABLE_NAME, 
						COLUMN_HASH + " = ?",
						new String[] { item.getLectureHash() } );
		
		close();
		return count != 0;
	}
	
	/**
	 * Get the LectureReviewItem if the user has
	 * reviewed item.
	 * 
	 * @param item
	 * The lecture the user may have reviewed
	 * 
	 * @return
	 * LectureReviewItem on success, null on failure.
	 * Null is returned MORE than !null, so check the
	 * return value.
	 */
	public LectureReviewItem getUserReview(LectureItem item) {
		LectureReviewItem review = null;
		
		open();
		Cursor cursor = mDatabase.query(
				TABLE_NAME, 
				new String[] { COLUMN_RATING, COLUMN_COMMENT, }, 
				COLUMN_HASH + " = \"" + item.getLectureHash() + "\"", 
				null, null, null, null);
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			int ratingIdx = cursor.getColumnIndex(COLUMN_RATING);
			int commentIdx = cursor.getColumnIndex(COLUMN_COMMENT);
			String ratingStr = cursor.getString(ratingIdx);
			String comment = cursor.getString(commentIdx);
			
			boolean[] rating = LectureReviewItem.getRatingArrayFromString(ratingStr);
			review = new LectureReviewItem(item, rating, comment, -1, null);
			cursor.close();
		}
		
		close();
		return review;
	}
}

















