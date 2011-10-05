package at.technikum.bicss.spp.cis;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import at.technikum.bicss.spp.cis.data.CISCredentialsManager;
import at.technikum.bicss.spp.cis.fragments.CalendarFragment;
import at.technikum.bicss.spp.cis.fragments.NewsFragment;

import com.jakewharton.android.viewpagerindicator.TitlePageIndicator;
import com.jakewharton.android.viewpagerindicator.TitleProvider;

public class MainActivity extends FragmentActivity implements ProgressbarProvider {
	static final int NUM_ITEMS = 2;
	
	private Context ctx;
	private MyAdapter mAdapter;
	private ViewPager mPager;

	private ImageView progressIcon;
	private TextView progressMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.ctx = this;
		
		this.setContentView(R.layout.main);

		if(!CISCredentialsManager.isInitialized()) CISCredentialsManager.initialize(this);
		
		this.mAdapter = new MyAdapter(getSupportFragmentManager());
		this.mPager = (ViewPager) findViewById(R.id.pager);
		this.mPager.setAdapter(this.mAdapter);

		this.progressIcon = (ImageView) findViewById(R.id.progressIcon);
		this.progressMessage = (TextView) findViewById(R.id.progressMessage);
		
		progressIcon.setVisibility(View.GONE);
		progressMessage.setText("Logged in as " + CISCredentialsManager.getUsername());
		 
		//Bind the title indicator to the adapter
		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		titleIndicator.setViewPager(mPager);


	}

	@Override
	public void startBackgroundProgress(String progessMessage) {
		this.progressMessage.setText(progessMessage);
		
		this.progressMessage.setVisibility(View.VISIBLE);
		this.progressIcon.setVisibility(View.VISIBLE);
		this.progressIcon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_infinite));
	}
	
	@Override
	public void progressFinished() {
		this.progressMessage.post(new Runnable() {

			@Override
			public void run() {
				progressIcon.clearAnimation();
				progressIcon.setVisibility(View.GONE);
				progressMessage.setText("Logged in as " + CISCredentialsManager.getUsername());
				
			}
			
		});
		
	}

	public class MyAdapter extends FragmentPagerAdapter implements TitleProvider {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			
			switch(position){
			case 0:
				fragment = CalendarFragment.newInstance((ProgressbarProvider)ctx);
				fragment.setHasOptionsMenu(true);
				break;
			case 1:
				fragment = NewsFragment.newInstance();
				break;
			default:
				fragment = null;
			}
			
			return fragment;
		}

		@Override
		public String getTitle(int position) {
			switch(position){
			case 0:
				return "Calendar";
			case 1:
				return "News";
			case 2:
				return "News2";
			case 3:
				return "News3";
			case 4:
				return "News4";
			default:
				return null;
			}
		}
	}

	
}
