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

public class TodayFragment extends Fragment implements 	OnParseCompleteListener,
														OnItemClickListener {
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
	private void refreshItems() {
		List<SubscriptionItem> subs = new ArrayList<SubscriptionItem>();
		subs.add(new SubscriptionItem("161571.182", "dunno"));
		
		TimeEditParser parser = new TimeEditParser(TimeEditParser.CONTENT_TIMETABLE);
		parser.setOnParseCompleteListener(this);
		TimeEditHTTP.getTimeTable(subs, parser);
	}

	
	@Override
	public void onTimeTableComplete(List<LectureItem> items) {
		Log.d("REFRESH COMPLETE", "YYOYOYOYOOY");
		mListAdapter.setLectureItems(items);
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Intent i = new Intent(view.getContext(), LectureRatingActivity.class);
		startActivity(i);
	}
}
