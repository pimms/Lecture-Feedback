package chipmunk.unlimited.feedback.revfeed;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.rating.LectureRatingActivity;
import chipmunk.unlimited.feedback.webapi.WebAPI;
import chipmunk.unlimited.feedback.webapi.WebAPI.GetFeedCallback;

/** 
 * @class FeedFragment
 * Fragment containing a list view which displays 
 * recent reviews.
 */
public class FeedFragment extends Fragment implements OnItemClickListener, 
													  GetFeedCallback {
	private static final String TAG = "FeedFragment";
	
	private FeedAdapter mFeedAdapter;
	private ListView mListView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);
		
		mListView = (ListView)rootView.findViewById(R.id.feed_list_view);
		mFeedAdapter = new FeedAdapter(container.getContext());
		mListView.setAdapter(mFeedAdapter);
		mListView.setOnItemClickListener(this);
		
		refreshItems();
			
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * Refresh the list of items via the WebAPI's getFeed call.
	 */
	public void refreshItems() {
		SubscriptionDatabase subDb = new SubscriptionDatabase(getActivity());
		
		WebAPI webApi = new WebAPI();
		webApi.getFeed(this, subDb.getSubscriptionList(), 0, 25);
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


	@Override
	public void onGetFeedSuccess(List<LectureReviewItem> items) {
		mFeedAdapter.setReviewItems(items);
		mFeedAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGetFeedFailure(String errorMessage) {
		Log.e(TAG, "GetFeed failed: " + errorMessage);
		mListView.setAdapter(mFeedAdapter);
		mFeedAdapter.setReviewItems(null);
	}
}
