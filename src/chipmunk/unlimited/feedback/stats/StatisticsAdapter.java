package chipmunk.unlimited.feedback.stats;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;
import chipmunk.unlimited.feedback.webapi.WebAPI;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;


/**
 * The adapter retrieves all subscribed courses from the database, 
 * and displays them in the list view.
 *
 * The adapter retrieves the votes for the courses, and updates
 * the list view when the votes arrive.
 */
public class StatisticsAdapter extends BaseAdapter
                               implements GetCourseVotesCallback {
    public interface StatisticsAdapterUpdateListener {
        public void onStatsAdapterUpdated(StatisticsAdapter adapter, boolean success);
    }

    private static final String TAG = "StatisticsAdapter";
	
	private List<SubscriptionItem> mSubscriptions;
    private List<CourseVote> mCourseVotes;
	private LayoutInflater mInflater;
	private Context mContext;

    private StatisticsAdapterUpdateListener mListener;

	
	public StatisticsAdapter(Context context) {
		mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

    public void setStatisticsAdapterUpdateListener(StatisticsAdapterUpdateListener li) {
        mListener = li;
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
		
		TextView tvName = (TextView)convertView.findViewById(R.id.course_text_view_name);
		TextView tvPos  = (TextView)convertView.findViewById(R.id.course_text_view_positive);
        TextView tvNeg  = (TextView)convertView.findViewById(R.id.course_text_view_negative);

		SubscriptionItem sub = (SubscriptionItem)getItem(position);
		tvName.setText(sub.getName());

        // Set the vote texts
        CourseVote vote = getCourseVote(sub.getHigCode());
        if (vote != null) {
            tvPos.setText("" + vote.getPositiveVoteCount());
            tvNeg.setText("" + vote.getNegativeVoteCount());
        } else {
            tvPos.setText("0");
            tvNeg.setText("0");
        }
		
		return convertView;
	}

    public CourseVote getCourseVote(String higCode) {
        CourseVote vote = null;

        if (mCourseVotes != null) {
            // Attempt to find the vote object
            for (CourseVote cv : mCourseVotes) {
                if (cv.getCourseCode().equals(higCode)) {
                    vote = cv;
                    break;
                }
            }
        }

        return vote;
    }

	
	public void reloadSubscriptions() {
		loadSubscriptions();
		notifyDataSetChanged();
	}
	
	private void loadSubscriptions() {
		SubscriptionDatabase db = new SubscriptionDatabase(mContext);
		mSubscriptions = db.getSubscriptionList();

        WebAPI webApi = new WebAPI();
        webApi.getCourseVotes(this, mSubscriptions);
	}


    @Override
    public void onGetCourseVotesSuccess(List<CourseVote> items) {
        mCourseVotes = items;
        notifyDataSetChanged();

        if (mListener != null) {
            mListener.onStatsAdapterUpdated(this, true);
        }
    }

    @Override
    public void onGetCourseVotesFailure(String errorMessage) {
        mCourseVotes = null;
        Log.e(TAG, "Failed to get course votes: " + errorMessage);

        if (mListener != null) {
            mListener.onStatsAdapterUpdated(this, false);
        }
    }
}


















