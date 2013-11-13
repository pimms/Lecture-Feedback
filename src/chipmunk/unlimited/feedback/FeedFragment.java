package chipmunk.unlimited.feedback;

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
import chipmunk.unlimited.feedback.subscription.SubscriptionDBHelper;
import chipmunk.unlimited.feedback.webapi.GetFeed.GetFeedCallback;
import chipmunk.unlimited.feedback.webapi.WebAPI;

/** 
 * @class FeedFragment
 * Fragment containing a list view which displays 
 * recent reviews.
 */
public class FeedFragment extends Fragment implements OnItemClickListener, 
													  GetFeedCallback {
	private static final String TAG = "FeedFragment";
	
	private FeedAdapter mFeedAdapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);
		
		ListView list = (ListView)rootView.findViewById(R.id.feed_list_view);
		mFeedAdapter = new FeedAdapter(container.getContext());
		list.setAdapter(mFeedAdapter);
		list.setOnItemClickListener(this);
		
		refreshItems();
		
		return rootView;
	}

	/**
	 * Refresh the list of items via the WebAPI's getFeed call.
	 */
	public void refreshItems() {
		SubscriptionDBHelper subDb = new SubscriptionDBHelper(getActivity());
		
		WebAPI webApi = new WebAPI();
		webApi.getFeed(this, subDb.getSubscriptionList(), 0, 25);
	}
	
	
	/**
	 * Start an instance of the LectureRatingActivity in a read-only state.
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View item, int position, long id) {
		Intent intent = new Intent(item.getContext(), LectureRatingActivity.class);
		
		/* Required parameters */
		intent.putExtra(LectureRatingActivity.PARAM_COURSE_NAME, "Placeholder");
		intent.putExtra(LectureRatingActivity.PARAM_LECTURER_NAME, "someone");
		intent.putExtra(LectureRatingActivity.PARAM_TIME, "sometime");
		intent.putExtra(LectureRatingActivity.PARAM_ROOM, "somewhere");
		
		/* Optional, READ ONLY toggling parameters */
		intent.putExtra(LectureRatingActivity.PARAM_READ_ONLY, true);
		intent.putExtra(LectureRatingActivity.PARAM_RATINGS, new boolean[] { false, false, true, true, false} );
		intent.putExtra(LectureRatingActivity.PARAM_COMMENT, "This was a great lecture.\nThe prof cursed at me a little.\n\n10/10, would attend again.");
		
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
	}
}
