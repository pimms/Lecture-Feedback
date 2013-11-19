package chipmunk.unlimited.feedback.revfeed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;

/**
 * Activity displaying a FeedFragment. This Activity
 * is used to display "uncommon" feeds, such as feeds
 * for a single course or a single lecture.
 *
 * One, and only one of the following parameters must
 * be passed in the spawning Intent.
 *
 * +----------------------+---------+--------+
 * | Parameter name       | Type    | Value  |
 * +----------------------+---------+--------+
 * | PARAM_SINGLE_COURSE  | boolean | TRUE   |
 * | PARAM_SINGLE_LECTURE | boolean | TRUE   |
 * +----------------------+---------+--------+
 *
 * If PARAM_SINGLE_COURSE is set, the following
 * parameters must also be set:
 *
 * Parameter name           Type    Value
 * PARAM_COURSE_NAME        String  The name of the course.
 * PARAM_COURSE_CODE        String  The HiG code of the course
 *
 * If PARAM_SINGLE_CODE is set, the following
 * parameters must also be set:
 * TODO!
 */
public class FeedActivity extends FragmentActivity {
    public static final String PARAM_SINGLE_COURSE = "single_course";
    public static final String PARAM_SINGLE_LECTURE = "single_lecture";

    /* PARAM_SINGLE_COURSE attributes */
    public static final String PARAM_COURSE_NAME = "course_name";
    public static final String PARAM_COURSE_CODE = "course_code";

    /* PARAM_SINGLE_LECTURE attributes */
    // TODO!

    private static final String TAG = "FeedActivity";

    private FeedFragment mFeedFragment;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_feed);

        /* Add the FeedFragment */
        mFeedFragment = new FeedFragment();
        mFeedFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_feed_layout, mFeedFragment)
                .commit();

        handleIntentParameters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_refresh:
                mFeedFragment.refreshContents();
                break;
        }

        return true;
    }


    private void handleIntentParameters() {
        boolean course;
        boolean lecture;

        Intent intent = getIntent();
        course = intent.getBooleanExtra(PARAM_SINGLE_COURSE, false);
        lecture = intent.getBooleanExtra(PARAM_SINGLE_LECTURE, false);

        if (course == lecture) {
            Log.e(TAG, "ONE PARAMETER MUST BE DEFINED.");
            finish();
        }

        if (course) {
            String name = intent.getStringExtra(PARAM_COURSE_NAME);
            String code = intent.getStringExtra(PARAM_COURSE_CODE);

            if (name == null || code == null) {
                Log.e(TAG, "COURSE_NAME and COURSE_CODE must be defined!");
                finish();
            }

            SubscriptionItem subItem = new SubscriptionItem(null, code, name);
            Feed feed = new Feed(this, mFeedFragment);
            feed.setStateCourse(subItem);
            mFeedFragment.setFeed(feed);

            getActionBar().setTitle(subItem.getName());
        }
    }
}
