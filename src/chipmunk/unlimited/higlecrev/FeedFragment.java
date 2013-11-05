package chipmunk.unlimited.higlecrev;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/** 
 * @class FeedFragment
 * Fragment containing a list view which displays 
 * recent reviews.
 */
public class FeedFragment extends Fragment implements OnItemClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);
		
		ListView list = (ListView)rootView.findViewById(R.id.feed_list_view);
		FeedAdapter adapter = new FeedAdapter(container.getContext(), null);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		return rootView;
	}

	
	@Override
	public void onItemClick(AdapterView<?> adapter, View item, int position, long id) {
		Intent i = new Intent(item.getContext(), LectureRatingActivity.class);
		startActivity(i);
	}
}
