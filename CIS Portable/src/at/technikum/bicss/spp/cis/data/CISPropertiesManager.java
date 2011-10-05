package at.technikum.bicss.spp.cis.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class CISPropertiesManager {

	// . . . . . . . . . . . . . . . . .  A P P L I C A T I O N  S P E C I F I C

	private static final String TAG = "CIS";
	private static CISPropertiesManager instance = null;
	private Context ctx;

	// . . . . . . . . . . . . . . . . . . . .  D A T A B A S E  S E T T I N G S

	private static final String TABLE_NAME = "properties";

	private SQLiteDatabase db;
	
	private SQLiteStatement insertStmt;
	private SQLiteStatement updateStmt;
	private SQLiteStatement selectStmt;
	private SQLiteStatement deleteStmt;
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . .  S Q L  Q U E R I E S

	public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " (name TEXT PRIMARY KEY, value TEXT)";

	public static final String DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

	public static final String INSERT = "INSERT INTO " + TABLE_NAME + " (name, value) values (?, ?)";

	public static final String UPDATE = "UPDATE " + TABLE_NAME + " SET value = ? WHERE name = ?";

	public static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE name = ?";

	public static final String SELECT = "SELECT value FROM " + TABLE_NAME + " WHERE name = ?";
	
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . C O N S T R U C T O R

	private CISPropertiesManager(Context context) {
		this.ctx = context;
		this.db = CISDatabaseManager.open(ctx);
		this.insertStmt = this.db.compileStatement(INSERT);
		this.updateStmt = this.db.compileStatement(UPDATE);
		this.selectStmt = this.db.compileStatement(SELECT);
		this.deleteStmt = this.db.compileStatement(DELETE);
	}
	
	public static CISPropertiesManager getInstance(Context context) {
		if(instance == null) instance = new CISPropertiesManager(context);
		return instance;
	}
	
	// . . . . . . . . . . . . . . . . . . .  D A T A B A S E  F U N C T I O N S

	/**
	 * Insert a preference into the database.
	 * 
	 * @param name Name of the preference
	 * @param value Value of the Preference
	 * @return The number of the newly inserted row, or -1 if insert statement
	 *         was unsuccessful
	 */
	private long insert(String name, String value) {
		this.insertStmt.bindString(1, name);
		this.insertStmt.bindString(2, value);
		return this.insertStmt.executeInsert();
	}

	private void update(String name, String value) {
		this.updateStmt.bindString(1, value);
		this.updateStmt.bindString(2, name);
		this.updateStmt.execute();
	}
	
	private String select(String name) {
		String result = null;
		
		this.selectStmt.bindString(1, name);
		
		try {
			result = this.selectStmt.simpleQueryForString(); 
			Log.i(TAG, "Fetched from database: " + name + " = " + result);
		} catch(SQLiteDoneException e) {
			Log.i(TAG, "There is no property '" + name + "' inside the database");
		}
		
		return result;
	}
	
	private void delete(String name) {
		this.deleteStmt.bindString(1, name);
		this.deleteStmt.execute();
	}
	
	
	// . . . . . . . . . . . . . . . P U B L I C  A C C E S S  F U N C T I O N S
	
	/**
	 * Sets a property inside the database. If the property already exists, it
	 * is overwritten. Otherwise the property is newly created.
	 * 
	 * @param name Name of the property to set
	 * @param value Value to set
	 */
	public void setProperty(String name, String value) {
		if(getProperty(name) == null) {
			insert(name, value);
		} else {
			update(name, value);
		}
	}
	
	/**
	 * Sets a boolean property inside the database. If the property already exists, it
	 * is overwritten. Otherwise the property is newly created.
	 * 
	 * @param name Name of the property to set
	 * @param value Boolean value to set
	 */
	public void setProperty(String name, Boolean value) {
		this.setProperty(name, String.valueOf(value));
	}
	
	/**
	 * Fetches a stored property from the database. If no property exists, or
	 * there was an general database error, <code>null</code> is returned.
	 * 
	 * @param name The name of the property to fetch from the database
	 * @return The value of the property, or <code>null</code> if there was an
	 *         error.
	 */
	public String getProperty(String name) {
		/**Cursor cursor = this.db.query(TABLE_NAME, new String[] { "value" },
			"name = ?", new String[] { name }, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			String value = cursor.getString(0);
			cursor.close();
			return value;
		} else {
			return null;
		}**/
		
		return this.select(name);
	}

	public boolean getPropertyBoolean(String name) {
		String value = this.select(name);
		if(value != null) {
			Log.i(TAG, "Boolean value of string is " + Boolean.parseBoolean(value));
			return Boolean.parseBoolean(value);
		} else {
			return false;
		}
	}
	
	/**
	 * Clears a property from the database.
	 * @param name Name of the property to unset
	 */
	public void unsetProperty(String name) {
		this.delete(name);
	}
	
	/*public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}*/

//	public List<String> selectAll() {
//		List<String> list = new ArrayList<String>();
//		Cursor cursor = this.db.query(TABLE_NAME, new String[] { "name" },
//			null, null, null, null, "name desc");
//
//		if (cursor.moveToFirst()) {
//			do {
//				list.add(cursor.getString(0));
//			} while (cursor.moveToNext());
//		}
//
//		if (cursor != null && !cursor.isClosed()) {
//			cursor.close();
//		}
//
//		return list;
//	}
}