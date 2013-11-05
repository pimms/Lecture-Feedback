package chipmunk.unlimited.higlecrev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @class FeedAdapter
 * Adapter class preparing raw feed-data to be displayed
 * in a list view.
 */
public class FeedAdapter extends BaseAdapter {
	private static LayoutInflater mInflater;
	
	public FeedAdapter(Context context, String[] data) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return 7;
	}
	
	@Override
	public Object getItem(int position) {
		return "bæsj";
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			vi = mInflater.inflate(R.layout.feed_review_item, null);
		}
		
		return vi;
	}
}
