package chipmunk.unlimited.feedback.stats;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;


/**
 * The adapter retrieves all subscribed courses from the database, 
 * and displays them in the list view. 
 */
public class StatisticsAdapter extends BaseAdapter {
	
	private List<SubscriptionItem> mSubscriptions;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public StatisticsAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		if (mSubscriptions == null) {
			loadSubscriptions();
		}
		
		return mSubscriptions.size();
	}

	@Override
	public Object getItem(int position) {
		return mSubscriptions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentGroup) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_course, null);
		}
		
		TextView tvName = (TextView)convertView.findViewById(R.id.stats_course_text_view_name);
		TextView tvCode = (TextView)convertView.findViewById(R.id.stats_course_text_view_code);
		
		SubscriptionItem sub = (SubscriptionItem)getItem(position);
		tvName.setText(sub.getName());
		tvCode.setText(sub.getHigCode());
		
		return convertView;
	}
	
	
	public void reloadSubscriptions() {
		loadSubscriptions();
		notifyDataSetChanged();
	}
	
	private void loadSubscriptions() {
		SubscriptionDatabase db = new SubscriptionDatabase(mContext);
		mSubscriptions = db.getSubscriptionList();
	}
}


















