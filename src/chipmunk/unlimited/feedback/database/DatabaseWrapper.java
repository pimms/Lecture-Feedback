package chipmunk.unlimited.feedback.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Superclass for all database interface-classes.
 */
public abstract class DatabaseWrapper extends SQLiteOpenHelper {
	protected static final String DB_NAME = "DBLectureFeedback";
	protected static final int DB_VERSION = 1;
	
	protected SQLiteDatabase mDatabase;
	
	
	public DatabaseWrapper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	/**
	 * Create all the tables in this database.
	 * 
	 * Create your table in a similar manner when adding
	 * more tables. 
	 */
	@Override
	public final void onCreate(SQLiteDatabase database) {
		database.execSQL(ReviewedLectureDatabase.TABLE_CREATE);
		database.execSQL(SubscriptionDatabase.TABLE_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int old, int n) {
		
	}

    /**
     * Open the database for read & write operations.
     * This sets mDatabase to a non-nil value.
     */
	public void open() {
		if (mDatabase == null) {
			mDatabase = getWritableDatabase();
		}
	}

    /**
     * Close the database handle and set mDatabase to nil.
     */
	public void close() {
		mDatabase.close();
		mDatabase = null;
	}
}
