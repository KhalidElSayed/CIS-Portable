package at.technikum.bicss.spp.cis.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import at.technikum.bicss.spp.cis.ProgressbarProvider;
import at.technikum.bicss.spp.cis.R;
import at.technikum.bicss.spp.cis.data.CISCalendarManager;
import at.technikum.bicss.spp.cis.data.CISCalendarManager.Event;

public class CalendarFragment extends Fragment {

	private static final String TAG = "CIS";
	private static final long ONE_HOUR = 60 * 60 * 1000L;

	private Context ctx;
	private ExpandableListView calendarListView;

	private ProgressbarProvider progressbarProvider;
	private TextView nextLessonDate;
	private TextView nextLessonName;
	private TextView nextLessonRoom;
	private TextView noNextLesson;

	private CISCalendarManager calendarManager;
	private CalendarAdapter calendarAdapter;

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static CalendarFragment newInstance(ProgressbarProvider progressbarProvider) {
		CalendarFragment f = new CalendarFragment();
		f.progressbarProvider = progressbarProvider;
		return f;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.ctx = view.getContext();

		this.calendarListView = (ExpandableListView) view.findViewById(R.id.calendarListView);
		this.nextLessonDate = (TextView) view.findViewById(R.id.nextLessonDate);
		this.nextLessonName = (TextView) view.findViewById(R.id.nextLessonName);
		this.nextLessonRoom = (TextView) view.findViewById(R.id.nextLessonRoom);
		this.noNextLesson = (TextView) view.findViewById(R.id.noNextLesson);

		this.calendarManager = new CISCalendarManager(this.ctx);
		this.calendarAdapter = new CalendarAdapter(this.calendarManager);
		//this.cm.refreshCalendars();


		this.calendarListView.setAdapter(this.calendarAdapter);

		this.updateNextLesson();
	}

	private void updateNextLesson() {
		Event event = this.calendarManager.getNextEvent();

		if(event == null) {
			this.noNextLesson.setVisibility(View.VISIBLE);
			this.nextLessonDate.setVisibility(View.GONE);
			this.nextLessonName.setVisibility(View.GONE);
			this.nextLessonRoom.setVisibility(View.GONE);
		} else {
			this.noNextLesson.setVisibility(View.GONE);
			this.nextLessonDate.setVisibility(View.VISIBLE);
			this.nextLessonName.setVisibility(View.VISIBLE);
			this.nextLessonRoom.setVisibility(View.VISIBLE);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			Date eventDate = null;
			try {
				eventDate = formatter.parse(event.getDate());
			} catch(ParseException e) {
				Log.e(TAG, "Error while formatting date", e);
			}

			String dateString = "";
			switch(daysBetween(eventDate, new Date())){
			case 0:
				dateString = getActivity().getString(R.string.today);
				break;
			case 1:
				dateString = getActivity().getString(R.string.tomorrow);
				break;
			default:
				dateString = DateFormat.format("E, dd.MM.", eventDate).toString();
			}

			dateString = dateString.concat(DateFormat.format(" (kk:mm)", eventDate).toString());

			this.nextLessonDate.setText(dateString);
			this.nextLessonName.setText(event.getName());
			this.nextLessonRoom.setText(event.getRoom());
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.calendar_menu, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.refreshCalendar:
			this.progressbarProvider.startBackgroundProgress("Refreshing");
			this.calendarManager.refreshCalendars(this.progressbarProvider);
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}



	private int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime() + ONE_HOUR) / (ONE_HOUR * 24));
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.calendar, container, false);
		return v;
	}

	private class CalendarAdapter implements ExpandableListAdapter {
		private CISCalendarManager cm;


		public CalendarAdapter(CISCalendarManager cm) {
			this.cm = cm;
		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			switch(childPosition){
			case 0:
				return ctx.getString(R.string.date) + ": " + this.cm.getEventByOffset(groupPosition)
					.getDate();
			case 1:
				return ctx.getString(R.string.room) + ": " + this.cm.getEventByOffset(groupPosition)
					.getRoom();
			case 2:
				return ctx.getString(R.string.teacher) + ": " + this.cm.getEventByOffset(
					groupPosition).getTeacher();
			}
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 64);
			TextView textView = new TextView(parent.getContext());
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(80, 0, 0, 0);
			textView.setText((String) getChild(groupPosition, childPosition));
			return textView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 3;
		}

		@Override
		public long getCombinedChildId(long groupId, long childId) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getCombinedGroupId(long groupId) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return this.cm.getEventByOffset(groupPosition).getDate();
		}

		@Override
		public int getGroupCount() {
			return new Long(cm.countEvents()).intValue();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 64);

			TextView textView = new TextView(parent.getContext());
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(80, 0, 0, 0);

			textView.setText((String) getGroup(groupPosition));

			return textView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onGroupCollapsed(int groupPosition) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			// TODO Auto-generated method stub

		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

	}
}