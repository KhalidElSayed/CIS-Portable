package at.technikum.bicss.spp.cis.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import at.technikum.bicss.spp.cis.helpers.SimpleCrypto;

/**
 * CISCredentialManager is a database interface for reading and writing username and password<p>
 * 
 * It relies on the <code>SimpleCrypto</code> class for encrypting the password and the username inside 
 * the database. The encryption does not prevent from reading and decrypting the password from 
 * the database, since the encryption key is also stored inside the database. The purpose for encryption
 * is to avoid storing the user's password and username in the clear. 
 *  
 * @author David Schreiber
 * @version 1.0
 * @see SimpleCrypto
 */
public class CISCredentialsManager {

	// . . . . . . . . . . . . . . . . . . . . . . . . . . .  A P P L I C A T I O N  S P E C I F I C

	private final static String TAG = "CIS";
	
	private Context ctx;
	private CISPropertiesManager db;


	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . C O N S T R U C T O R S

	/**
	 * Public constructor for creating the credential manager.<p>
	 * Needs the application context and the database for storing and retreiving credentials.
	 * 
	 * @param context application context (or activity context)
	 * @param database SQLiteDatabase for credential storage
	 * @see Context
	 * @see SQLiteDatabase
	 */
	public CISCredentialsManager(Context context, SQLiteDatabase database) {
		this.ctx = context;
		this.db = new CISPropertiesManager(context, database);
	}
	

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .  P U B L I C  M E T H O D S

	public String getUsername() {
		return getDecryptedProperty("username");
	}

	public String getPassword() {
		return getDecryptedProperty("password");
	}

	public void setUsername(String username) {
		String cleanUsername = username.trim().toLowerCase();
		setEncryptedProperty("username", cleanUsername);
	}

	public void setPassword(String password) {
		setEncryptedProperty("password", password);
	}

	public void clearUsername() {
		getDb().unsetProperty("username");
	}

	public void clearPassword() {
		getDb().unsetProperty("password");
	}

	public void setAutoLogin(boolean autoLogin) {
		db.setProperty("autoLogin", autoLogin);
	}
	
	public void setRememberUsername(boolean rememberUsername) {
		db.setProperty("rememberUsername", rememberUsername);
	}
	
	public boolean getAutoLogin() {
		return db.getPropertyBoolean("autoLogin");
	}
	
	public boolean getRememberUsername() {
		return db.getPropertyBoolean("rememberUsername");
	}
	
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .  I N T E R N A L  M E T H O D S

	private CISPropertiesManager getDb() {
		if(db == null)
			db = CISPropertiesManager.getInstance(ctx);
		return db;
	}

	private String getMasterKey() {
		String masterKey = getDb().getProperty("masterKey");

		if(masterKey == null) {
			masterKey = SimpleCrypto.generateSeed();
			getDb().setProperty("masterKey", masterKey);
		}

		return masterKey;
	}

	private String getDecryptedProperty(String propertyName) {
		String masterKey = getMasterKey();
		String encryptedValue = getDb().getProperty(propertyName);
		String value = null;

		try {
			value = SimpleCrypto.decrypt(masterKey, encryptedValue);
		} catch(Exception e) {
			Log.e(TAG, "Error while decrypting property '" + propertyName + "'", e);
		}

		return value;
	}

	private void setEncryptedProperty(String propertyName, String plainValue) {
		String masterKey = getMasterKey();

		try {
			String encryptedValue = SimpleCrypto.encrypt(masterKey, plainValue);
			getDb().setProperty(propertyName, encryptedValue);
		} catch(Exception e) {
			Log.e(TAG, "Error while encrypting property '" + propertyName + "'", e);
		}
	}
}
