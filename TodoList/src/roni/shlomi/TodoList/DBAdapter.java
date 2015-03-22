// ------------------------------------ DBADapter.java ---------------------------------------------

// TODO: Change the package to match your project.
package roni.shlomi.TodoList;

import android.R.bool;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class DBAdapter {

	/////////////////////////////////////////////////////////////////////
	//	Constants & Data
	/////////////////////////////////////////////////////////////////////
	// For logging:
	private static final String TAG = "DBAdapter";

	// DB Fields
	public static final String KEY_ROWID = "_id";
	public static final int COL_ROWID = 0;
	
	

	public static final String KEY_LAT = "latitude";
	public static final String KEY_LONG = "longitude";
	/*
	 * CHANGE 1:
	 */
	// TODO: Setup your fields here:
	public static final String KEY_TASK = "task";
	public static final String KEY_VISITED = "visit";
	

	// TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
	public static final int COL_TASK = 1;
	public static final int COL_LAT = 2;
	public static final int COL_LONG = 3;
	public static final int COL_VISITED = 4;

	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_TASK, KEY_LAT,KEY_LONG,KEY_VISITED};

	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "MyDb";
	public static final String DATABASE_TABLE = "mainTable";
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 13;	

	private static final String DATABASE_CREATE_SQL = 
			"create table " + DATABASE_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_LONG  + " TEXT, "
			+ KEY_LAT + " TEXT, "
			+ KEY_VISITED + " INTEGER, "

			/*
			 * CHANGE 2:
			 */
			// TODO: Place your fields here!
			// + KEY_{...} + " {type} not null"
			//	- Key is the column name you created above.
			//	- {type} is one of: text, integer, real, blob
			//		(http://www.sqlite.org/datatype3.html)
			//  - "not null" means it is a required field (must be given a value).
			// NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
			+ KEY_TASK + " text not null"

			// Rest  of creation:
			+ ");";

	// Context of application who uses us.
	private final Context context;

	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/////////////////////////////////////////////////////////////////////
	//	Public methods:
	/////////////////////////////////////////////////////////////////////

	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}

	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}

	// Add a new set of values to the database.
	public long insertRow(String task, String latitude, String longitude,int visit) {
		
		/*
		 * CHANGE 3:
		 */		
		// TODO: Update data in the row with new fields.
		// TODO: Also change the function's arguments to be what you need!
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASK, task);

		/*-------*/
		
		initialValues.put(KEY_LAT, latitude);
		initialValues.put(KEY_LONG, longitude);
		initialValues.put(KEY_VISITED, visit);
		
		Log.v("INSERT-latitudeDB",latitude);
		/*-------*/

		// Insert it into the database.
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	// Change an existing row to be equal to new data.
	public boolean  updateRow(long rowId, String task, String userChosenLat, String userChosenLong,int visit) {
		String where = KEY_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
		// TODO: Update data in the row with new fields.
		// TODO: Also change the function's arguments to be what you need!
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_TASK, task);
		newValues.put(KEY_LAT, userChosenLat);
		newValues.put(KEY_LONG, userChosenLong);
		newValues.put(KEY_VISITED, visit);
		Log.v("UPDATE-latitudeDB",userChosenLat);
		// Insert it into the database.
			//db.delete(DATABASE_TABLE, where, null);
			//return db.insert(DATABASE_TABLE, null, newValues);
		return db.update(DATABASE_TABLE, newValues, where, null) == 0;
	}

	// Delete a row from the database, by rowId (primary key)
	public boolean deleteRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(DATABASE_TABLE, where, null) != 0;
	}

	public void deleteAll() {
		Cursor c = getAllRows();
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(c.getLong((int) rowId));				
			} while (c.moveToNext());
		}
		c.close();
	}

	// Return all data in the database.
	public Cursor getAllRows() {
		String where = null;
		Cursor c =  db.query(true, DATABASE_TABLE, ALL_KEYS, 
				where, null, null, null, KEY_ROWID, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public int getIthRowId(int position) {
		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, 
				null, null, null, null, null, null);
		int i = 0;
		if (c.moveToFirst()) {
			do
			{
				if(i==position) 
					return c.getInt(DBAdapter.COL_ROWID);
				i++;
			}
			while(c.moveToNext());
			
		}
		return -1;
	}
	
	public double getLAT(int position) {
		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, 
				null, null, null, null, null, null);
		int i = 0;
		if (c.moveToFirst()) {
			do
			{
				if(i==position) 
					return c.getDouble(DBAdapter.COL_LAT);
				i++;
			}
			while(c.moveToNext());
			
		}
		return -1;
	}
	
	public double getLONG(int position) {
		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, 
				null, null, null, null, null, null);
		int i = 0;
		if (c.moveToFirst()) {
			do
			{
				if(i==position) 
					return c.getDouble(DBAdapter.COL_LONG);
				i++;
			}
			while(c.moveToNext());
			
		}
		return -1;
	}

	// Get a specific row (by rowId)
	public Cursor getRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, 
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}




	/////////////////////////////////////////////////////////////////////
	//	Private Helper Classes:
	/////////////////////////////////////////////////////////////////////

	/**
	 * Private class which handles database creation and upgrading.
	 * Used to handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			
			_db.execSQL(DATABASE_CREATE_SQL);			
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data!");
		//	 if(_db == null || !_db.isOpen() || _db.isReadOnly()) {
			//	 _db = getWritableDatabase();
			//    }
			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

			// Recreate new database:
			onCreate(_db);
		}
	}
}
