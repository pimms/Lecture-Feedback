package chipmunk.unlimited.feedback.revfeed;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.UpdateableFragment;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.rating.LectureRatingActivity;

/**
 * Fragment containing a list view which displays 
 * recent reviews.
 *
 * FeedFragment supports the same parameters FeedActivity
 * does.
 */
public class FeedFragment extends UpdateableFragment
        implements OnItemClickListener,Feed.FeedListener {
	private static final String TAG = "FeedFragment";

    private Feed mFeed;
	private FeedAdapter mFeedAdapter;
	private ListView mListView;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);
		return rootView;
	}
    @Override
    public void onActivityCreated(Bundle bundle) {
        mListView = getListView();

        /* Create the Feed */
        if (mFeed == null) {
            mFeed = new Feed(getActivity(), this);
        }

        /* Create the adapter and adjust it to mFeed's state */
        mFeedAdapter = new FeedAdapter(getActivity());
        mFeedAdapter.setFeedState(mFeed.getState());

        mListView.setAdapter(mFeedAdapter);
        mListView.setOnItemClickListener(this);

        refreshContents();
        super.onActivityCreated(bundle);
    }

    public void setFeed(Feed feed) {
        mFeed = feed;
        mFeed.setFeedListener(this);
    }


	/**
	 * Refresh the list of items via the WebAPI's getFeed call.
	 */
    @Override
    public void doRefresh() {
        mFeed.update(0, 25);
	}
    @Override
    public void onFeedUpdate(List<LectureReviewItem> items) {
        mFeedAdapter.setReviewItems(items);
        onUpdateCompleted();
    }


	@Override
	public void onItemClick(AdapterView<?> adapter, View item, int position, long id) {		
        if (mFeedAdapter.getFeedState() == Feed.STATE_DEFAULT ||
            mFeedAdapter.getFeedState() == Feed.STATE_LECTURE) {
            startReadOnlyLectureRatingActivity(position);
        }
	}
    /**
     * Start an instance of the LectureRatingActivity in a read-only state.
     */
    private void startReadOnlyLectureRatingActivity(int reviewItemIndex) {
        Intent intent = new Intent(getActivity(), LectureRatingActivity.class);

        LectureReviewItem reviewItem = (LectureReviewItem)mFeedAdapter.getItem(reviewItemIndex);

		/* Required parameters */
        intent.putExtra(LectureRatingActivity.PARAM_COURSE_NAME, reviewItem.getCourseName());
        intent.putExtra(LectureRatingActivity.PARAM_COURSE_HIG_CODE, reviewItem.getCourseHigCode());
        intent.putExtra(LectureRatingActivity.PARAM_LECTURER_NAME, reviewItem.getLecturer());
        intent.putExtra(LectureRatingActivity.PARAM_TIME, reviewItem.getTimeString());
        intent.putExtra(LectureRatingActivity.PARAM_DATE, reviewItem.getDateString());
        intent.putExtra(LectureRatingActivity.PARAM_ROOM, reviewItem.getRoom());

		/* Optional, READ ONLY toggling parameters */
        intent.putExtra(LectureRatingActivity.PARAM_READ_ONLY, true);
        intent.putExtra(LectureRatingActivity.PARAM_RATINGS, reviewItem.getRatings());
        intent.putExtra(LectureRatingActivity.PARAM_COMMENT, reviewItem.getComment());
        intent.putExtra(LectureRatingActivity.PARAM_REVIEW_ID, reviewItem.getId());
        intent.putExtra(LectureRatingActivity.PARAM_CLONE_COUNT, reviewItem.getCloneCount());

        startActivity(intent);
    }
}
