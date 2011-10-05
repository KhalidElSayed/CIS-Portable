package at.technikum.bicss.spp.cis.fragments;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.technikum.bicss.spp.cis.R;
import at.technikum.bicss.spp.cis.widgets.RoundedExpandableListView;

public class NewsFragment extends Fragment {

	private static final String TAG = "CIS";

	private Context ctx;
	private RoundedExpandableListView newsListView;
	private RSSFeed feed;

	public static NewsFragment newInstance() {
		NewsFragment f = new NewsFragment();
		return f;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.refreshRssFeed();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.news, container, false);

		this.ctx = container.getContext();
		this.newsListView = (RoundedExpandableListView) v.findViewById(R.id.newsListView);

		return v;
	}

	private void refreshRssFeed() {
		String rssURI = "http://www.technikum-wien.at/cms/rss.php";
		AsyncRssDownloadTask task = new AsyncRssDownloadTask();
		task.execute(rssURI);
	}

	private void rssFeedSuccessfullyLoaded(RSSFeed feed) {
		this.feed = feed;
		this.feed.sortByDate();
		this.newsListView.setAdapter(new RSSFeedAdapter());
	}

	private void setErrorWhileLoadingFeed() {
		//TODO Implement better error message!
		Toast t = Toast.makeText(ctx, "Could not retreive RSS news feed!", Toast.LENGTH_LONG);
		t.show();
	}

	private class AsyncRssDownloadTask extends AsyncTask<String, Integer, RSSFeed> {

		@Override
		protected RSSFeed doInBackground(String... params) {
			RSSReader reader = new RSSReader();
			RSSFeed feed = null;

			try {
				feed = reader.load(params[0]);
			} catch(RSSReaderException e) {
				Log.e(TAG, "Error while loading RSS news feed", e);
				this.cancel(true);
			}

			return feed;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			setErrorWhileLoadingFeed();
		}

		@Override
		protected void onPostExecute(RSSFeed result) {
			super.onPostExecute(result);
			rssFeedSuccessfullyLoaded(result);
		}
	}

	private class RSSFeedAdapter implements ExpandableListAdapter {

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return feed.getItem(groupPosition).getDescription();
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			return getGroupContent(groupPosition);
		}

		@Override
		public long getCombinedChildId(long groupId, long childId) {
			return childId;
		}

		@Override
		public int getGroupCount() {
			return feed.getCount();
		}

		@Override
		public long getGroupId(int pos) {
			return pos;
		}

		@Override
		public View getGroupView(int groupPosition, boolean arg1, View arg2, ViewGroup arg3) {
			return getGroupHeader(groupPosition);
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public long getCombinedGroupId(long groupId) {
			return groupId;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return feed.getItem(groupPosition).getDescription();
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

		@Override
		public boolean isEmpty() {
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

		public View getGroupContent(final int groupPosition) {
			AbsListView.LayoutParams groupParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			LinearLayout groupContent = new LinearLayout(ctx);
			groupContent.setOrientation(LinearLayout.HORIZONTAL);
			groupContent.setLayoutParams(groupParams);
			groupContent.setPadding(15, 0, 15, 0);
			groupContent.setGravity(Gravity.CENTER_VERTICAL);
			groupContent.setClickable(true);
			
			ImageView linkImageView = new ImageView(ctx);
			linkImageView.setLayoutParams(linearLayoutParams);
			linkImageView.setImageDrawable(ctx.getResources().getDrawable(R.drawable.goto_icon));
			linkImageView.setPadding(15, 15, 15, 15);
			groupContent.addView(linkImageView);

			TextView groupText = new TextView(ctx);
			groupText.setLayoutParams(linearLayoutParams);
			groupText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			groupText.setPadding(10, 15, 15, 15);
			groupText.setText(feed.getItem(groupPosition).getDescription());
			groupContent.addView(groupText);

			groupContent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// Fix bug in RSS Feed
					Uri link = feed.getItem(groupPosition).getLink();
					link = Uri.parse(link.toString().replace("?detail=", "?eventdetail="));
					
					// Send intent
					Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
					openBrowserIntent.setData(link);
					startActivity(openBrowserIntent);
				}
			});
			return groupContent;
			
			/*int viewHeight = isGroup ? 64 : AbsListView.LayoutParams.WRAP_CONTENT;
			// Layout parameters for the ExpandableListView

			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, viewHeight);

			TextView textView = new TextView(ctx);
			textView.setLayoutParams(layoutParams);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			if(isGroup) {
				textView.setPadding(35, 0, 0, 0);
			} else {
				textView.setPadding(50, 15, 15, 15);
			}

			Log.i(TAG, text);

			textView.setText(text);

			return textView;*/
		}

		private View getGroupHeader(int groupPosition) {
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams linParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			LinearLayout groupHeader = new LinearLayout(ctx);
			groupHeader.setOrientation(LinearLayout.HORIZONTAL);
			groupHeader.setLayoutParams(layoutParams);
			groupHeader.setMinimumHeight(64);
			groupHeader.setPadding(15, 0, 15, 0);
			groupHeader.setGravity(Gravity.CENTER_VERTICAL);

			TextView dateView = new TextView(ctx);
			String dateString = DateFormat.format("E, dd.MM.",
				feed.getItem(groupPosition).getPubDate()).toString();
			dateView.setText(dateString);
			dateView.setLayoutParams(linParams);
			dateView.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.round_blue));
			dateView.setTextColor(Color.WHITE);
			dateView.setTextSize(12);
			dateView.setPadding(15, 5, 15, 5);
			dateView.setMinWidth(130);
			dateView.setGravity(Gravity.CENTER);
			groupHeader.addView(dateView);

			TextView groupTitle = new TextView(ctx);
			groupTitle.setLayoutParams(linParams);
			groupTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			groupTitle.setPadding(35, 0, 15, 0);
			groupTitle.setText(feed.getItem(groupPosition).getTitle());
			groupHeader.addView(groupTitle);

			return groupHeader;
		}


	}
}