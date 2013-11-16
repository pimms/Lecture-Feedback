package chipmunk.unlimited.feedback;

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
	
	@Override
	public abstract void onCreate(SQLiteDatabase database);
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int old, int n) {
		
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
}
