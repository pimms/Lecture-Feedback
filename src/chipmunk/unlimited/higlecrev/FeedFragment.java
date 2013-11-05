package chipmunk.unlimited.higlecrev;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/** 
 * @class FeedFragment
 * Fragment containing a list view which displays 
 * recent reviews.
 */
public class FeedFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);
		
		ListView list = (ListView)rootView.findViewById(R.id.feed_list_view);
		FeedAdapter adapter = new FeedAdapter(container.getContext(), null);
		list.setAdapter(adapter);
		
		return rootView;
	}
}
