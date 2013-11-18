package chipmunk.unlimited.feedback.revfeed;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.MainActivityFragmentInterface;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.rating.LectureRatingActivity;
import chipmunk.unlimited.feedback.webapi.WebAPI;
import chipmunk.unlimited.feedback.webapi.WebAPI.GetFeedCallback;

/**
 * Fragment containing a list view which displays 
 * recent reviews.
 *
 * FeedFragment supports the same parameters FeedActivity
 * does.
 */
public class FeedFragment extends Fragment implements OnItemClickListener,
                                                      Feed.FeedListener,
                                                      MainActivityFragmentInterface {
	private static final String TAG = "FeedFragment";

    private Feed mFeed;
	private FeedAdapter mFeedAdapter;
	private ListView mListView;
	private ProgressBar mProgressBar;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);

   		mListView = (ListView)rootView.findViewById(R.id.feed_list_view);
		mProgressBar = (ProgressBar)rootView.findViewById(R.id.feed_progress_bar);
		
		mFeedAdapter = new FeedAdapter(container.getContext());
		mListView.setAdapter(mFeedAdapter);
		mListView.setOnItemClickListener(this);

        mFeed = new Feed(getActivity(), this);

		refreshContents();
			
		return rootView;
	}


    /**
     * Return the feed to allow external objects to
     * modify it.
     *
     * @return
     * OOOOH, IT'S WAFFLE TIME, IT'S WAFFLE TIME - WON'T
     * YOU HAVE SOME WAFFLES OF MINE?
     */
    public Feed getFeed() {
        return mFeed;
    }


	/**
	 * Refresh the list of items via the WebAPI's getFeed call.
	 */
    @Override
    public void refreshContents() {
        mFeed.update(0, 25);
		showProgressBar();
	}

    @Override
    public void onFeedUpdate(List<LectureReviewItem> items) {
        mFeedAdapter.setReviewItems(items);
        hideProgressBar();
    }


	/**
	 * Start an instance of the LectureRatingActivity in a read-only state.
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View item, int position, long id) {		
		Intent intent = new Intent(item.getContext(), LectureRatingActivity.class);
		
		LectureReviewItem reviewItem = (LectureReviewItem)mFeedAdapter.getItem(position);
		
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
		
		startActivity(intent);
	}

	
	private void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	private void hideProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}
}
