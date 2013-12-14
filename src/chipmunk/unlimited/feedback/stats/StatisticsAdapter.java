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
import chipmunk.unlimited.feedback.webapi.HttpClient;
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
    private boolean mTutorial;

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
			loadCourses();
		}

        if (!mTutorial) {
            return mSubscriptions.size();
        }

        return 1;
	}
	@Override
	public Object getItem(int position) {
        if (!mTutorial) {
		    return mSubscriptions.get(position);
        }

        return null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return HttpClient.isInternetAvailable(mContext);
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parentGroup) {
        if (mTutorial) {
            return getTutorialView(convertView);
        }

		if (convertView == null || convertView.getId() != R.layout.list_item_course) {
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

    private View getTutorialView(View convertView) {
        if (convertView == null || convertView.getId() != R.layout.tutorial) {
            convertView = mInflater.inflate(R.layout.tutorial, null);
        }

        TextView tvTitle = (TextView)convertView.findViewById(R.id.tutorial_text_view_title);
        TextView tvDesc  = (TextView)convertView.findViewById(R.id.tutorial_text_view_desc);

        tvTitle.setText(mContext.getResources().getString(
                R.string.frag_stats_tutorial_title_no_subs));
        tvDesc.setText(mContext.getResources().getString(
                R.string.frag_stats_tutorial_desc_no_subs));

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

    /**
     * Public method for reloading the content of the List View.
     */
	public void reloadCourses() {
        loadCourses();
        notifyDataSetChanged();
    }
    /**
     * Load all the courses and their votes via the web API. This
     * discards existing items.
     */
    private void loadCourses() {
        SubscriptionDatabase db = new SubscriptionDatabase(mContext);
        mSubscriptions = db.getSubscriptionList();

        /* Display the tutorial view if no subs exists */
        mTutorial = (mSubscriptions.size() == 0);

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
        Log.e(TAG, "Failed to get course votes: " + errorMessage);

        if (mListener != null) {
            mListener.onStatsAdapterUpdated(this, false);
        }
    }
}


















