package chipmunk.unlimited.feedback.highscore;

import android.app.ListActivity;
import android.os.Bundle;

import chipmunk.unlimited.feedback.R;

/**
 * Activity displaying the highest rated courses in descending
 * order.
 */
public class TopCourseActivity extends ListActivity {
    TopCourseAdapter mAdapter;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_listview);

        mAdapter = new TopCourseAdapter(this);
        getListView().setAdapter(mAdapter);
    }
}
