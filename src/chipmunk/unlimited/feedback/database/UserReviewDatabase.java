package chipmunk.unlimited.feedback.database;

import android.content.Context;

/**
 * Database holding the values from the local
 * user's reviews.
 * 
 * The data is stringified to some extent, and is saved on the form:
 * 
 * 	+---------------+-----------------+----------------------------+
 *  | HASH 			| RATINGS 		  | COMMENTS 				   |
 *  +---------------+-----------------+----------------------------+
 *  | <SHA1-val>	| 1.0.0.1.1 	  | Great lecture, proffy 	   |
 *  +---------------+-----------------+----------------------------+
 *
 *  Note that COLUMN_HASH references the hash entry in a 
 *  ReviewedLectureDatabase row. 
 */
public class UserReviewDatabase extends DatabaseWrapper {
	private static final String TAG = "UserReviewDatabase";
	
	public static final String TABLE_NAME 		= "LocalReviews";
	public static final String COLUMN_ID  		= "_id";
	public static final String COLUMN_HASH 		= "hash";
	public static final String COLUMN_RATINGS 	= "ratings";
	public static final String COLUMN_COMMENT 	= "comment";
	
	public static final String TABLE_CREATE =
			"CREATE TABLE IF NOT EXISTS " 
			+ TABLE_NAME 	 + " ( " 
			+ COLUMN_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_HASH 	 + " TEXT NOT NULL, "
			+ COLUMN_RATINGS + " TEXT NOT NULL, " 
			+ COLUMN_COMMENT + " TEXT "
			+ " );";
	
	
	public UserReviewDatabase(Context context) {
		super(context);
	}
	
	
	
}
