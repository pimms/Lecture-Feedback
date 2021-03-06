package chipmunk.unlimited.feedback;

import java.util.Locale;

import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.highscore.TopCourseActivity;
import chipmunk.unlimited.feedback.stats.StatisticsFragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import chipmunk.unlimited.feedback.revfeed.FeedFragment;
import chipmunk.unlimited.feedback.subscription.AddSubscriptionFragment;
import chipmunk.unlimited.feedback.subscription.SubscriptionFragment;
import chipmunk.unlimited.feedback.subscription.SubscriptionProtocolListener;
import chipmunk.unlimited.feedback.today.TodayFragment;
import chipmunk.unlimited.feedback.webapi.HttpClient;


public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener,
        SubscriptionProtocolListener {
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	/* Fragments included in the pane-view */
	private FeedFragment mFeedFragment;
	private TodayFragment mTodayFragment;
	private StatisticsFragment mStatsFragment;
	
	/* After leaving and returning, the fragments
	 * should be updated. */
	private boolean mShouldUpdateFragments = false;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity);

        new LaunchPrompt(this).onActivityCreate();
        showNoInternetWarning();

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(2);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (mShouldUpdateFragments) {
			refreshFragments();	
			mShouldUpdateFragments = false;
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		mShouldUpdateFragments = true;

        if (!isFinishing()) {
            new LaunchPrompt(this).onActivityPause();
        }
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_subscriptions:
				showSubscriptionFragment();
				break;
			case R.id.action_main_refresh:
				refreshFragments();
				break;
            case R.id.action_topcourses:
                showTopCoursesActivity();
                break;
            case R.id.action_main_about:
                showAboutDialog();
                break;
		}
		
		return true;
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}


    private void showSubscriptionFragment() {
        SubscriptionFragment sub = new SubscriptionFragment(this);
        sub.show(getFragmentManager(), "fragment_subscription");
    }
    @Override
    public void onAddSubscriptionRequest(SubscriptionFragment toDismiss) {
        toDismiss.dismiss();

        AddSubscriptionFragment addSub = new AddSubscriptionFragment(this);
        addSub.show(getFragmentManager(), "add_subscription_fragment");
    }
    @Override
	public void onSubscriptionsChanged() {
		refreshFragments();
	}
    @Override
    public void onAddSubscriptionFragmentDismiss(AddSubscriptionFragment toDismiss) {
        toDismiss.dismiss();
    }

    private void refreshFragments() {
		if (mTodayFragment == null) {
			mTodayFragment = (TodayFragment)mSectionsPagerAdapter.getItem(0);
		}
		
		if (mFeedFragment == null) {
            mFeedFragment = (FeedFragment)mSectionsPagerAdapter.getItem(1);
		}

        if (mStatsFragment == null) {
            mStatsFragment = (StatisticsFragment)mSectionsPagerAdapter.getItem(2);
        }

        if (HttpClient.isInternetAvailable(this)) {
            mTodayFragment.refreshContents();
            mFeedFragment.refreshContents();
            mStatsFragment.refreshContents();
        }
	}


    private void showTopCoursesActivity() {
        Intent intent = new Intent(this, TopCourseActivity.class);
        startActivity(intent);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources r = getResources();

        builder.setTitle(r.getString(R.string.about_title));
        builder.setMessage(r.getString(R.string.about_description));
        builder.setNegativeButton(r.getString(R.string.about_back), null);

        builder.show();
    }

    private void showNoInternetWarning() {
        if (!HttpClient.isInternetAvailable(this)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.no_internet_dialog_title))
                    .setMessage(getString(R.string.no_internet_dialog_message))
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            switch (position) {
                case 0:
                    if (mTodayFragment == null) {
                        mTodayFragment = new TodayFragment();
                    }
                    fragment = mTodayFragment;
                    break;
                case 1:
                    if (mFeedFragment == null) {
                        mFeedFragment = new FeedFragment();
                    }
                    fragment = mFeedFragment;
                    break;
                case 2:
                    if (mStatsFragment == null) {
                        mStatsFragment = new StatisticsFragment();
                    }
                    fragment = mStatsFragment;
                    break;
                default:
				/* TODO: Handle error */
                    fragment = null;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getResources().getString(R.string.main_tab_today);
                case 1:
                    return getResources().getString(R.string.main_tab_feed);
                case 2:
                    return getResources().getString(R.string.main_tab_stats);
            }
            return null;
        }
    }
}
