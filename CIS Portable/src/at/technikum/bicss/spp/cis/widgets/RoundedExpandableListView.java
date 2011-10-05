package at.technikum.bicss.spp.cis.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import at.technikum.bicss.spp.cis.R;

public class RoundedExpandableListView extends ExpandableListView { // implements OnGroupExpandListener {

	public RoundedExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//		setOnGroupExpandListener(this);
		initialize();
	}

	public RoundedExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//		setOnGroupExpandListener(this);
		initialize();
	}

	public RoundedExpandableListView(Context context) {
		super(context);
		//		setOnGroupExpandListener(this);
		initialize();
	}

	private void initialize() {
		
		if(isInEditMode()) editModeSetup();
	}

	private void editModeSetup() {
		this.setAdapter(new ExpandableListAdapter() {
			
			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGroupExpanded(int groupPosition) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGroupCollapsed(int groupPosition) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
				TextView v = new TextView(getContext());
				LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
				v.setText("Group " + groupPosition);
				v.setLayoutParams(layoutParams);
				v.setGravity(Gravity.CENTER_VERTICAL);
				return v;
			}
			
			@Override
			public long getGroupId(int groupPosition) {
				// TODO Auto-generated method stub
				return groupPosition;
			}
			
			@Override
			public int getGroupCount() {
				return 3;
			}
			
			@Override
			public Object getGroup(int groupPosition) {
				return "Group " + groupPosition;
			}
			
			@Override
			public long getCombinedGroupId(long groupId) {
				// TODO Auto-generated method stub
				return groupId;
			}
			
			@Override
			public long getCombinedChildId(long groupId, long childId) {
				return childId;
			}
			
			@Override
			public int getChildrenCount(int groupPosition) {
				return 3;
			}
			
			@Override
			public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
				TextView v = new TextView(getContext());
				v.setText("Item " + groupPosition + "-" + childPosition);
				v.setPadding(30, 5, 0, 5);
				return v;
			}
			
			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}
			
			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return "Item " + groupPosition + "-" + childPosition;
			}
			
			@Override
			public boolean areAllItemsEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		this.expandGroup(1);
		this.setDivider(getResources().getDrawable(R.drawable.grey_line));
		this.setChildDivider(getResources().getDrawable(R.drawable.grey_line));
		this.setDividerHeight(2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float radius = 25.0f;

		Path clipPath = new Path();
		RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
		clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
		canvas.clipPath(clipPath);

		super.onDraw(canvas);
	}

}
