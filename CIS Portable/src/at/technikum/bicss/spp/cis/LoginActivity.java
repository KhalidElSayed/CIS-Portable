package at.technikum.bicss.spp.cis;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.technikum.bicss.spp.cis.data.CISCredentialsManager;
import at.technikum.bicss.spp.cis.data.CISPropertiesManager;

/**
 * Login form for entering username and password which are granting CIS access.
 * 
 * @author david
 * 
 */
public class LoginActivity extends Activity {

	// . . . . . . . . . . . . . . . . . . . . . . . . . . .  A P P L I C A T I O N  S P E C I F I C

	/** Logcat tag */
	private static final String TAG = "CIS";

	/** Activity context for access inside events */
	private Context ctx;

	/** Database connection **/
	private CISPropertiesManager db;
	private CISCredentialsManager cd;

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .  L A Y O U T  V I E W S
	private EditText usernameInputBox;
	private EditText passwordInputBox;
	private CheckBox rememberUsernameCheckBox;
	private CheckBox autoLoginCheckBox;
	private Button loginButton;


	// . . . . . . . . . . . . . . . . . . . . . . . . . .  A P P L I C A T I O N  L I F E C Y C L E

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Store context for further access
		this.ctx = this;

		// Initialize the credentials manager (Important!)
		CISCredentialsManager.initialize(this.ctx);

		// Create new Property manager
		this.db = CISPropertiesManager.getInstance(this);

		// Initialize all UI elements
		initializeGUI();

		// Check if app was restored (eg. after orientation change)
		if(restoreSession(savedInstanceState) == false) {
			loadCredentialsFromDatabase();
			if(this.db.getPropertyBoolean("autoLogin") == true)
				this.doAutoLogin();


		}


	}

	private void loadCredentialsFromDatabase() {
		if(this.db.getPropertyBoolean("rememberUsername") == true) {
			this.usernameInputBox.setText(CISCredentialsManager.getUsername());
			this.rememberUsernameCheckBox.setChecked(true);
		} else {
			this.rememberUsernameCheckBox.setChecked(false);
		}

		if(this.db.getPropertyBoolean("autoLogin") == true) {
			this.passwordInputBox.setText(CISCredentialsManager.getPassword());
			this.autoLoginCheckBox.setChecked(true);
		} else {
			this.autoLoginCheckBox.setChecked(false);
		}
	}

	private boolean restoreSession(Bundle savedInstanceState) {
		if(savedInstanceState != null && savedInstanceState.getString("username") != null && savedInstanceState.getString("password") != null) {
			this.usernameInputBox.setText(savedInstanceState.getString("username"));
			this.passwordInputBox.setText(savedInstanceState.getString("password"));
			this.rememberUsernameCheckBox.setChecked(savedInstanceState.getBoolean("rememberUsername"));
			this.autoLoginCheckBox.setChecked(savedInstanceState.getBoolean("autoLogin"));
			return true;
		}
		return false;
	}

	private void initializeGUI() {
		/*
		 * TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
		 * Spannable welcomeTextSpannable = new SpannableString(
		 * "Willkommen in CIS PORTABLE, der mobilen App der FH Technikum Wien");
		 * welcomeTextSpannable.setSpan( new
		 * ForegroundColorSpan(getResources().getColor(R.color.fh_blue)), 18,
		 * 26, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		 * welcomeTextSpannable.setSpan(new StyleSpan(Typeface.BOLD), 14, 26,
		 * Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		 * welcomeText.setText(welcomeTextSpannable);
		 * 
		 * TextView stepOne = (TextView) findViewById(R.id.stepOne); Spannable
		 * stepOneSpan = new
		 * SpannableString("1st Step - Enter login credentials");
		 * stepOneSpan.setSpan(new AbsoluteSizeSpan(30, true), 0, 1,
		 * Spannable.SPAN_INCLUSIVE_INCLUSIVE); stepOneSpan.setSpan(new
		 * AbsoluteSizeSpan(15, true), 1, 34,
		 * Spannable.SPAN_INCLUSIVE_INCLUSIVE); stepOne.setText(stepOneSpan);
		 * 
		 * TextView stepTwo = (TextView) findViewById(R.id.stepTwo); Spannable
		 * stepTwoSpan = new SpannableString("2nd Step - Choose login details");
		 * stepTwoSpan.setSpan(new AbsoluteSizeSpan(30, true), 0, 1,
		 * Spannable.SPAN_INCLUSIVE_INCLUSIVE); stepTwoSpan.setSpan(new
		 * AbsoluteSizeSpan(15, true), 1, 31,
		 * Spannable.SPAN_INCLUSIVE_INCLUSIVE); stepTwo.setText(stepTwoSpan);
		 * 
		 * TextView stepThree = (TextView) findViewById(R.id.stepThree);
		 * Spannable stepThreeSpan = new
		 * SpannableString("3rd Step - Enjoy CIS Portable");
		 * stepThreeSpan.setSpan(new AbsoluteSizeSpan(30, true), 0, 1,
		 * Spannable.SPAN_INCLUSIVE_INCLUSIVE); stepThreeSpan.setSpan(new
		 * AbsoluteSizeSpan(15, true), 1, 29,
		 * Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		 * stepThree.setText(stepThreeSpan);
		 */
		this.usernameInputBox = (EditText) findViewById(R.id.usernameInputBox);
		this.passwordInputBox = (EditText) findViewById(R.id.passwordInputBox);
		this.rememberUsernameCheckBox = (CheckBox) findViewById(R.id.rememberUsernameCheckBox);
		this.autoLoginCheckBox = (CheckBox) findViewById(R.id.autoLoginCheckBox);
		this.loginButton = (Button) findViewById(R.id.loginButton);

		this.rememberUsernameCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// If user unchecks box, remove username from database
				if(!((CheckBox) v).isChecked())
					updateCredentialsInDatabase();
			}
		});

		this.autoLoginCheckBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// If user unchecks box, remove password from database
				if(((CheckBox) v).isChecked())
					showPasswordStorageWarning();
				else
					updateCredentialsInDatabase();
			}
		});

		this.loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doLogin();
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("username", this.usernameInputBox.getText().toString());
		outState.putString("password", this.passwordInputBox.getText().toString());
		outState.putBoolean("rememberUsername", this.rememberUsernameCheckBox.isChecked());
		outState.putBoolean("autoLogin", this.autoLoginCheckBox.isChecked());
	}

	private void doAutoLogin() {
		Intent i = new Intent(ctx, MainActivity.class);
		ctx.startActivity(i);
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .  A C T I V I T Y  S P E C I F I C

	/**
	 * Runs the login process. This includes input validation, login testing and
	 * updating the credentials stored in the database.
	 */
	private void doLogin() {

		if(validateLoginInputFields()) {
			final String username = this.usernameInputBox.getText().toString();
			final String password = this.passwordInputBox.getText().toString();

			final ProgressDialog dialog = ProgressDialog.show(this, "",
				getString(R.string.login_progress), true);

			new AsyncTask<String, String, Boolean>() {

				@Override
				protected void onPostExecute(Boolean result) {
					if(result) {
						updateCredentialsInDatabase();
						Toast toaster = Toast.makeText(ctx, R.string.login_sucessful,
							Toast.LENGTH_SHORT);
						toaster.show();
						dialog.dismiss();

						Intent i = new Intent(ctx, MainActivity.class);
						ctx.startActivity(i);

					} else {
						Toast toaster = Toast.makeText(ctx, R.string.login_failed,
							Toast.LENGTH_SHORT);
						toaster.show();

						dialog.dismiss();
					}


				}

				@Override
				protected Boolean doInBackground(String... params) {
					return testLoginCredentials(params[0], params[1]);
				}
			}.execute(username, password);
		} else {
			Toast toaster = Toast.makeText(ctx, R.string.credentials_incomplete, Toast.LENGTH_SHORT);
			toaster.show();
		}
	}

	/**
	 * Tests if something is written inside username and password fields.
	 * 
	 * @return <code>true</code> if username and password is entered, otherwise
	 *         <code>false</code>
	 */
	private boolean validateLoginInputFields() {
		// Trim username before testing - this prevents spaced username
		String username = this.usernameInputBox.getText().toString();
		this.usernameInputBox.setText(username.trim());

		// Test against zero-length
		if(this.usernameInputBox.getText().length() == 0 || this.passwordInputBox.getText()
			.length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	private void showPasswordStorageWarning() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(
			"Activating automatic login may reduce security (especially on rooted phones). Do you really want to activate automatic login?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Do nothing
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					autoLoginCheckBox.setChecked(false);
					dialog.cancel();
				}
			});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void updateCredentialsInDatabase() {

		String username = this.usernameInputBox.getText().toString();
		String password = this.passwordInputBox.getText().toString();

		boolean rememberUsername = this.rememberUsernameCheckBox.isChecked();
		boolean autoLogin = this.autoLoginCheckBox.isChecked();

		CISCredentialsManager.setUsername(username);
		CISCredentialsManager.setPassword(password);

		if(rememberUsername || autoLogin) {

			this.db.setProperty("rememberUsername", "true");

			if(autoLogin) {
				this.db.setProperty("autoLogin", "true");
			} else {
				this.db.unsetProperty("autoLogin");
			}

		} else {
			this.db.unsetProperty("rememberUsername");
			this.db.unsetProperty("autoLogin");
		}

		Log.i(TAG, "Updated credentials");
	}

	public boolean testLoginCredentials(String username, String password) {

		// Connection URL (this is the secured menu)
		String url = "http://cis.technikum-wien.at/cis/menu.php?content_id=173";

		// Create a new HttpClient
		DefaultHttpClient httpclient = new DefaultHttpClient();

		// The variable holding the response
		HttpResponse response = null;

		// Create an HTTP request, fetching the resource from the given URL
		HttpGet request = new HttpGet(url);

		// Set username and password (Basic Authorization)
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
			new UsernamePasswordCredentials(username, password));

		try {
			// Execute HTTP Post Request
			response = httpclient.execute(request);
		} catch(ClientProtocolException e) {
			Log.e("CIS", "Error", e);
		} catch(IOException e) {
			Log.e("CIS", "Error", e);
		}

		if(response != null && response.getStatusLine().getStatusCode() == 200)
			return true;
		else
			return false;
	}
}
