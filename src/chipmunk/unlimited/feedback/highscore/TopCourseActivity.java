package chipmunk.unlimited.feedback.highscore;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.ScrollToRefreshListView;
import chipmunk.unlimited.feedback.revfeed.FeedActivity;
import chipmunk.unlimited.feedback.stats.CourseLecturesActivity;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;

/**
 * Activity displaying the highest rated courses in descending
 * order.
 */
public class TopCourseActivity extends ListActivity
        implements  ScrollToRefreshListView.OnScrollToRefreshListener,
                    TopCourseAdapter.OnTopCourseAdapterUpdateListener {
    TopCourseAdapter mAdapter;
    ScrollToRefreshListView mRefreshListView;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_scroll2refresh_listview);

        mAdapter = new TopCourseAdapter(this);
        mAdapter.setCallback(this);
        getListView().setAdapter(mAdapter);

        getActionBar().setTitle(getResources().getString(R.string.activity_top_course_title));

        mRefreshListView = (ScrollToRefreshListView)getListView();
        mRefreshListView.setOnScrollToRefreshListener(this);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        CourseVote vote = (CourseVote)mAdapter.getItem(position);

        Intent intent = new Intent(this, CourseLecturesActivity.class);
        intent.putExtra(CourseLecturesActivity.PARAM_COURSE_CODE, vote.getCourseCode());
        intent.putExtra(CourseLecturesActivity.PARAM_COURSE_NAME, vote.getCourseName());
        startActivity(intent);
    }

    /**
     * Notify the adapter to load more courses when the list view has scrolled
     * all the way to the bottom.
     *
     * @param view
     * The ScrollToRefreshView.
     */
    @Override
    public void onScrollToLoad(ScrollToRefreshListView view) {
        mAdapter.loadMoreCourses();
    }

    /**
     * When the adapter has finished loading, notify the list view that
     * the refresh finished.
     *
     * @param tpa
     * The adapter that finished updating.
     */
    @Override
    public void onTopCourseAdapterUpdateComplete(TopCourseAdapter tpa) {
        mRefreshListView.onRefreshComplete();
    }
}
