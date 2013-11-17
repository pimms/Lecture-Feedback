package chipmunk.unlimited.feedback;

import java.util.ArrayList;
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
import chipmunk.unlimited.feedback.TimeEditParser.OnParseCompleteListener;
import chipmunk.unlimited.feedback.database.ReviewedLectureDatabase;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;

public class TodayFragment extends Fragment implements 	OnParseCompleteListener,
														OnItemClickListener {
	private static final String TAG = "TodayFragment";
	
	private DailyLectureAdapter mListAdapter;
	private ListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_today, container, false);
		
		mListAdapter = new DailyLectureAdapter(rootView.getContext());
		mListView = (ListView)rootView.findViewById(R.id.today_list_view);
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(this);
		refreshItems();
			
		return rootView;
	}
	
	/**
	 * Reload the list view with updated lectures.
	 */
	public void refreshItems() {
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
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		LectureItem lecture = (LectureItem)mListAdapter.getItem(position);
		
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
		}
		
		startActivity(intent);
	}
}
