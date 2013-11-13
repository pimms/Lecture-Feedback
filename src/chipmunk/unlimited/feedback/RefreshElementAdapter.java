package chipmunk.unlimited.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Adapter for a ListView providing only a single item
 * containing a loading-gif.
 */
public class RefreshElementAdapter extends BaseAdapter {
	private static LayoutInflater sInflater;
	
	public RefreshElementAdapter(Context context) {
		sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		if (view == null) {
			view = sInflater.inflate(R.layout.list_item_loading, null);
		}
		
		return view;
	}

}
