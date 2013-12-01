package chipmunk.unlimited.feedback.today;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.UpdateableFragment;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.TimeEditHTTP;
import chipmunk.unlimited.feedback.TimeEditParser;
import chipmunk.unlimited.feedback.TimeEditParser.OnParseCompleteListener;
import chipmunk.unlimited.feedback.database.ReviewedLectureDatabase;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.rating.LectureRatingActivity;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;

public class TodayFragment extends UpdateableFragment
        implements 	OnParseCompleteListener, OnItemClickListener {
	private static final String TAG = "TodayFragment";
	
	private DailyLectureAdapter mListAdapter;
	private ListView mListView;
	
	private ProgressBar mProgressBar;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_today, container, false);

		mListView = (ListView)rootView.findViewById(R.id.today_list_view);
		mProgressBar = (ProgressBar)rootView.findViewById(R.id.today_progress_bar);
		
		mListAdapter = new DailyLectureAdapter(rootView.getContext());
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(this);
		
		Log.d(TAG, "TodayFragment # onCreateView()");

        refreshContents();
        onFragmentInitialized();
		return rootView;
	}
    @Override
    protected void doRefresh() {
        showProgressBar();

        SubscriptionDatabase db = new SubscriptionDatabase(getActivity());
        List<SubscriptionItem> subs = db.getSubscriptionList();

        TimeEditParser parser = new TimeEditParser(TimeEditParser.CONTENT_TIMETABLE);
        parser.setOnParseCompleteListener(this);
        TimeEditHTTP.getTimeTable(subs, parser);
    }

	
	@Override
	public void onTimeTableParsingComplete(List<LectureItem> items) {
		mListAdapter.setLectureItems(items);
		mListAdapter.notifyDataSetChanged();
		
		hideProgressBar();
	}
	@Override
	public void onTimeTableParsingFailed(String errorMessage) {
		Log.e(TAG, "Failed to get TimeEdit data: " + errorMessage);
		hideProgressBar();
	}
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		LectureItem lecture = (LectureItem)mListAdapter.getItem(position);
		
		/* If the view was a separator, lecture will be null */
		if (lecture == null) {
			return;
		}
		
		Intent intent = new Intent(view.getContext(), LectureRatingActivity.class);
		
		/* TODO: WELL THIS CAN JOLLY WELL BE IMPROVED, INNIT? */
		intent.putExtra(LectureRatingActivity.PARAM_COURSE_NAME, lecture.getCourseName());
		intent.putExtra(LectureRatingActivity.PARAM_COURSE_HIG_CODE, lecture.getCourseHigCode());
		intent.putExtra(LectureRatingActivity.PARAM_LECTURER_NAME, lecture.getLecturer());
		intent.putExtra(LectureRatingActivity.PARAM_TIME, lecture.getTimeString());
		intent.putExtra(LectureRatingActivity.PARAM_DATE, lecture.getDateString());
		intent.putExtra(LectureRatingActivity.PARAM_ROOM, lecture.getRoom());
		
		/* Check if the user has reviewed the lecture */
		ReviewedLectureDatabase db = new ReviewedLectureDatabase(getActivity());
		LectureReviewItem review = db.getUserReview(lecture);
		if (review != null) {
			// Add optional READONLY parameters
			intent.putExtra(LectureRatingActivity.PARAM_READ_ONLY, true);
			intent.putExtra(LectureRatingActivity.PARAM_RATINGS, review.getRatings());
			intent.putExtra(LectureRatingActivity.PARAM_COMMENT, review.getComment());
            intent.putExtra(LectureRatingActivity.PARAM_REVIEW_ID, 0);

            /* Don't send a clone count, as the clone count is not stored locally */
            intent.putExtra(LectureRatingActivity.PARAM_CLONE_COUNT, 0);
		}
		
		startActivity(intent);
	}

	
	private void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	private void hideProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}
}
