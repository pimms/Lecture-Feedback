package chipmunk.unlimited.feedback.revfeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import chipmunk.unlimited.feedback.R;

/**
 * Activity displaying a FeedFragment. This Activity
 * is used to display "uncommon" feeds, such as feeds
 * for a single course or a single lecture.
 *
 * One, and only one of the following parameters must
 * be passed in the spawning Intent.
 *
 * +----------------------+--------+-----------------------------------+
 * | Parameter name       | Type   | Value                             |
 * +----------------------+--------+-----------------------------------+
 * | PARAM_SINGLE_COURSE  | String | The HiG course code to display.   |
 * | PARAM_SINGLE_LECTURE | TBI    | TBI                               |
 * +----------------------+--------+-----------------------------------+
 *
 */
public class FeedActivity extends FragmentActivity {
    public static final String PARAM_SINGLE_COURSE = "single_course";
    public static final String PARAM_SINGLE_LECTURE = "single_lecture";

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


    private void handleIntentParameters() {
        String course;
        String lecture;

        Intent intent = getIntent();
        course = intent.getStringExtra(PARAM_SINGLE_COURSE);
        lecture = intent.getStringExtra(PARAM_SINGLE_LECTURE);

        if ( (course != null) == (lecture != null) ) {
            Log.e(TAG, "ONE PARAMETER MUST BE DEFINED.");
            finish();
        }


    }
}
