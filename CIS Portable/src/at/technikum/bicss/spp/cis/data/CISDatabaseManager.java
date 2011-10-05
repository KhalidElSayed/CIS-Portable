package at.technikum.bicss.spp.cis.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CISDatabaseManager {
	private static CISOpenHelper openHelper = null;
	private static SQLiteDatabase db = null;
	private static boolean dbOpened = false;
	
	public static SQLiteDatabase open(Context context) {
		if(openHelper == null) {
			openHelper = new CISOpenHelper(context);
		}
		
		if(dbOpened == false) {
			db = openHelper.getWritableDatabase();
			dbOpened = true;
		}
		
		return db;
	}
	
	public static void close() {
		if(dbOpened == true) {
			db.close();
			dbOpened = false;
		}
	}
	
	/**
	 * This is the database helper class which handles creation of a new database as
	 * well as updating the database to a newer version.
	 * 
	 * Version 1.0 of this class only creates a new database. On upgrade this
	 * version drops the complete database and re-creates it, without preserving
	 * already stored records.
	 * 
	 * @version 1.0
	 * 
	 * @author David Schreiber
	 * 
	 * @todo Update OpenHelper to a newer version which preserves records when a
	 *       database upgrade is needed.
	 * 
	 */
	private static class CISOpenHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "at.technikum.bic.cis.portable";
		private static final int DATABASE_VERSION = 1;

		public CISOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create all Tables needed inside the application
			db.execSQL(CISCalendarManager.CREATE);
			db.execSQL(CISPropertiesManager.CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Drop all tables
			db.execSQL(CISCalendarManager.DROP);
			db.execSQL(CISPropertiesManager.DROP);
			// Run create
			this.onCreate(db);
		}

	}
}
