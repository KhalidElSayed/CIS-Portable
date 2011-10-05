package at.technikum.bicss.spp.cis.data;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;
import at.technikum.bicss.spp.cis.ProgressbarProvider;

public class CISCalendarManager {
	// . . . . . . . . . . . . . . . . .  A P P L I C A T I O N  S P E C I F I C

	private static final String TAG = "CIS";
	private Context ctx;

	// . . . . . . . . . . . . . . . . . . . .  D A T A B A S E  S E T T I N G S

	
	private static final String TABLE_NAME = "calendar";

	private SQLiteDatabase db;

	private SQLiteStatement insertStmt;
	private SQLiteStatement updateStmt;
	private SQLiteStatement deleteStmt;
	private SQLiteStatement deleteAllStmt;
	private SQLiteStatement countStmt;


	// . . . . . . . . . . . . . . . . . . . . . . . . . .  S Q L  Q U E R I E S

	public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " (date TEXT PRIMARY KEY, name TEXT, room TEXT, teacher TEXT)";

	public static final String DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

	public static final String INSERT = "INSERT INTO " + TABLE_NAME + " (date, name, room, teacher) values (?, ?, ?, ?)";

	public static final String UPDATE = "UPDATE " + TABLE_NAME + " SET name = ?, room = ?, teacher = ? WHERE date = ?";

	public static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE date = ?";

	public static final String DELETE_ALL = "DELETE FROM " + TABLE_NAME;

	public static final String COUNT = "SELECT COUNT(*) FROM " + TABLE_NAME;


	// . . . . . . . . . . . . . . . . . . . . . . . . . . C O N S T R U C T O R

	public CISCalendarManager(Context context) {
		this.ctx = context;
		this.db = CISDatabaseManager.open(this.ctx);
		this.insertStmt = this.db.compileStatement(INSERT);
		this.updateStmt = this.db.compileStatement(UPDATE);
		this.deleteStmt = this.db.compileStatement(DELETE);
		this.deleteAllStmt = this.db.compileStatement(DELETE_ALL);
		this.countStmt = this.db.compileStatement(COUNT);
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
	private long insert(String date, String name, String room, String teacher) {
		this.insertStmt.bindString(1, date);
		this.insertStmt.bindString(2, name);
		this.insertStmt.bindString(3, room);
		this.insertStmt.bindString(4, teacher);
		long result = this.insertStmt.executeInsert();
		Log.i(TAG, "Insert done!");
		return result;

	}

	private void update(String date, String name, String room, String teacher) {
		this.updateStmt.bindString(1, name);
		this.updateStmt.bindString(2, room);
		this.updateStmt.bindString(3, teacher);
		this.updateStmt.bindString(4, date);
		this.updateStmt.execute();
	}

	private void delete(String date) {
		this.deleteStmt.bindString(1, date);
		this.deleteStmt.execute();
	}

	private void deleteAll() {
		this.deleteAllStmt.execute();
	}


	// . . . . . . . . . . . . . . . P U B L I C  A C C E S S  F U N C T I O N S

	/**
	 * Add an event to the database. If there is already an event with the given
	 * date, the event is updated. Otherwise the event is newly created.
	 * 
	 * @param date The actual date of the event
	 * @param name The name of the event
	 * @param room The room or location of the event
	 * @param teacher The teacher who is holding the event
	 * 
	 * @see #getEvent(String)
	 */
	public void addEvent(String date, String name, String room, String teacher) {
		if(getEventByDate(date) == null) {
			insert(date, name, room, teacher);
		} else {
			update(date, name, room, teacher);
		}
	}

	/**
	 * Fetch a stored event from the database. If no event exists for the given
	 * date, or there was an general database error, <code>null</code> is
	 * returned.
	 * 
	 * @param date The exact date of the event to fetch from the database
	 * @return The event, or <code>null</code> if there was an error.
	 * 
	 * @see Event
	 * 
	 * @todo <b>NOT IMPLEMENTED!</b> - THIS CLASS ALWAYS RETURNS NULL
	 */
	public Event getEventByDate(String date) {
		this.insertStmt.bindString(1, date);
		Cursor c = this.db.query(TABLE_NAME, new String[] { "name", "room", "teacher" },
			"strftime('%s', date) = strftime('%s', ?)", new String[] { date }, null, null, null);
		
		if(c != null && c.moveToFirst()) {
			Event e = new Event(date, c.getString(0), c.getString(1), c.getString(2));
			c.close();
			return e;
		}
		return null;
	}

	/**
	 * Clears a property from the database.
	 * 
	 * @param name Name of the property to unset
	 */
	public void unsetEvent(String date) {
		this.delete(date);
	}

	/**
	 * Returns the count of Events inside the database.
	 * 
	 * @return Number of events
	 */
	public long countEvents() {
		return countStmt.simpleQueryForLong();
	}

	public Event getNextEvent() {
		Cursor c = db.query(TABLE_NAME, new String[] { "date", "name", "room", "teacher" },
			"strftime('%s', date) >= strftime('%s', 'now')", null, null, null, "date ASC");

		if(c != null && c.moveToFirst()) {
			Event e = new Event(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
			c.close();
			return e;
		}

		return null;
	}

	/**
	 * Fetches the event with the given offset out of the database.
	 * <ul>
	 * <li>The events are retreived ordered by their dates.</li>
	 * <li>
	 * The offset is zero based. Passing <code>0</code> as offset will retreive
	 * the first element of all stored events.</li>
	 * </ul>
	 * 
	 * @param offset Zero-based integer value addressing an event
	 * @return The event read from the database, or <code>null</code> if no
	 *         event was found.
	 * 
	 * @see #getNextEvent() CalendarManager.getNextEvent()
	 * @see Event CalendarManager.Event
	 */
	public Event getEventByOffset(int offset) {
		Cursor c = db.query(TABLE_NAME, new String[] { "date", "name", "room", "teacher" }, null,
			null, null, null, "date(date) ASC", offset + ",1");

		if(c != null && c.moveToFirst()) {
			Event e = new Event(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
			c.close();
			return e;
		}

		return null;
	}


	public void refreshCalendars(final ProgressbarProvider progressbarProvider) {
		new AsyncTask<String, String, String> (){

			@Override
			protected String doInBackground(String... params) {
				InputStream is = loadICalStream();
				Calendar cal = createCalendarFromStream(is);
				clearEventsFromDatabase();
				writeCalendarToDatabase(cal);
				progressbarProvider.progressFinished();
				return null;
			}
			
		}.execute();
	}

	// . . . . . . . . . . . . . . . . . . . . N E T W O R K   F U N C T I O N S

	private InputStream loadICalStream() {
		String username = CISCredentialsManager.getUsername();
		String password = CISCredentialsManager.getPassword();

		// Connection URL (this is the calendar)
		String url = "http://cis.technikum-wien.at/cis/private/lvplan/stpl_kalender.php?type=student&pers_uid=" + username + "&ort_kurzbz=&stg_kz=258&sem=5&ver=B&grp=2&gruppe_kurzbz=&begin=1314828000&ende=1328310000&format=ical&version=2&target=ical";

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

		try {
			return response.getEntity().getContent();
		} catch(Exception e) {
			Log.e(TAG, "Error while doing HTTP-Get to fetch iCal stream", e);
		}

		return null;
	}

	private Calendar createCalendarFromStream(InputStream is) {
		Calendar cal = null;
		CalendarBuilder cab = new CalendarBuilder();

		if(is != null) {
			try {
				cal = cab.build(is);
				return cal;
			} catch(Exception e) {
				Log.e(TAG, "Error while loading and parsing iCal stream", e);
			}
		}

		return null;
	}

	

	private void clearEventsFromDatabase() {
		deleteAll();
	}


	private void writeCalendarToDatabase(Calendar cal) {
		if(cal != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
			for(Object o : cal.getComponents(Component.VEVENT)) {
				VEvent event = (VEvent) o;
	
				String summary = event.getSummary().getValue();
				String description = event.getDescription().getValue();
				
				// Retreive the date and format it to SQLite style
				String date = formatter.format((event.getStartDate().getDate()));
				// Retreive the first part of the summary (which is the name of the class)
				String name = summary.split("\\s+")[0];
				// Retreive the second part of the description (which is the name of the teacher)
				String teacher = description.split("\\n+")[1];
				// Retreive the room and replace the underscore by a blank
				String room = event.getLocation().getValue().replace('_', ' ');
	
				addEvent(date, name, room, teacher);
			}
		}
	}


	public List<Event> getAllEvents() {
		// TODO NOT YET IMPLEMENTED!
		return null;
	}


	// . . . . . . . . . . . . . . . . . . P U B L I C  I N N E R  C L A S S E S

	/**
	 * This is a bean for storing calendar event data. It provides storage
	 * capabilities for event date, name of the event, the room or place of the
	 * event and the teacher who is holding the event.
	 * 
	 * @author David Schreiber
	 * 
	 */
	public static class Event {
		private String date;
		private String name;
		private String room;
		private String teacher;

		public Event(String date, String name, String room, String teacher) {
			super();
			this.date = date;
			this.name = name;
			this.room = room;
			this.teacher = teacher;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getRoom() {
			return room;
		}

		public void setRoom(String room) {
			this.room = room;
		}

		public String getTeacher() {
			return teacher;
		}

		public void setTeacher(String teacher) {
			this.teacher = teacher;
		}

	}

}
