package chipmunk.unlimited.feedback.highscore;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.revfeed.FeedActivity;
import chipmunk.unlimited.feedback.stats.CourseLecturesActivity;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;

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

        getActionBar().setTitle(getResources().getString(R.string.activity_top_course_title));
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        CourseVote vote = (CourseVote)mAdapter.getItem(position);

        Intent intent = new Intent(this, CourseLecturesActivity.class);
        intent.putExtra(CourseLecturesActivity.PARAM_COURSE_CODE, vote.getCourseCode());
        intent.putExtra(CourseLecturesActivity.PARAM_COURSE_NAME, vote.getCourseName());
        startActivity(intent);
    }
}
