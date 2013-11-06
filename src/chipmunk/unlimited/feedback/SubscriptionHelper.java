package chipmunk.unlimited.feedback;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @class SubscriptionHelper
 * Class providing an interface to the subscription
 * table in the local database. 
 */
public class SubscriptionHelper extends SQLiteOpenHelper {
	private static final String TAG = "SubscriptionHelper";
	
	private static final String TABLE_NAME 	= "Subscription";
	private static final String COLUMN_ID 	= "id";
	private static final String COLUMN_NAME = "name";
	
	private static final String DB_NAME = "LectureReview";
	private static final int DB_VERSION = 1;
	
	private static final String TABLE_CREATE = 
			"create table if not exists "
			+ TABLE_NAME + " ( "
			+ COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NAME + "TEXT NOT NULL "
			+ ");";

	public SubscriptionHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	
	
	public void addSubscription(String name) {
		SQLiteDatabase db = getReadableDatabase();
		
		final ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, name);
		
		long insertId = db.insert(TABLE_NAME, null, values);
		db.close();
		
		if (insertId == -1) {
			Log.e(TAG, "Failed to insert '" + name + "' into DB");
		}
	}
}

















