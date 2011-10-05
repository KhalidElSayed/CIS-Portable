package at.technikum.bicss.spp.cis.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import at.technikum.bicss.spp.cis.R;

public class CalendarView extends TableLayout {

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.calendar_view, this, true);
		
	}

	public CalendarView(Context context) {
		super(context);
	}
	
	

}
