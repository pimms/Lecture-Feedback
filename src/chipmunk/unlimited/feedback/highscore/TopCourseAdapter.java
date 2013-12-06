package chipmunk.unlimited.feedback.highscore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.webapi.WebAPI;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;

public class TopCourseAdapter extends BaseAdapter
        implements GetTopCoursesCallback {
    /**
     * Notification interface for when the adapter has retrieved
     * more CourseVote items from the web API.
     */
    public interface OnTopCourseAdapterUpdateListener {
        public void onTopCourseAdapterUpdateComplete(TopCourseAdapter tpa);
    }


    private static final String TAG = "TopCourseAdapter";


    private LayoutInflater mInflater;
    private Context mContext;
    private OnTopCourseAdapterUpdateListener mListener;
    private List<CourseVote> mCourseVotes;
    private boolean mLoadingMore;


    public TopCourseAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reloadCourses();
    }

    public void setCallback(OnTopCourseAdapterUpdateListener listener) {
        mListener = listener;
    }


    @Override
    public int getCount() {
        if (mCourseVotes != null) {
            return mCourseVotes.size();
        }

        return 0;
    }
    @Override
    public Object getItem(int i) {
        if (mCourseVotes != null) {
            return mCourseVotes.get(i);
        }

        return null;
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null || view.getId() != R.layout.list_item_course) {
            view = mInflater.inflate(R.layout.list_item_course, null);
        }

        TextView tvName = (TextView)view.findViewById(R.id.course_text_view_name);
        TextView tvPos  = (TextView)view.findViewById(R.id.course_text_view_positive);
        TextView tvNeg  = (TextView)view.findViewById(R.id.course_text_view_negative);

        CourseVote vote = (CourseVote)getItem(i);

        tvName.setText(vote.getCourseName());

        if (vote == null) {
            tvPos.setText("?");
            tvNeg.setText("?");
        } else {
            tvPos.setText("" + vote.getPositiveVoteCount());
            tvNeg.setText("" + vote.getNegativeVoteCount());
        }

        return view;
    }

    /**
     * Discard the existing set of items and reload them.
     */
    public void reloadCourses() {
        WebAPI webApi = new WebAPI();
        webApi.getTopCourses(this, 0, 25);
    }
    /**
     * Load more items and append them to the set of existing items.
     */
    public void loadMoreCourses() {
        mLoadingMore = true;

        WebAPI webApi = new WebAPI();
        webApi.getTopCourses(this, mCourseVotes.size(), 25);
    }


    @Override
    public void onGetTopCoursesSuccess(List<CourseVote> courses) {
        if (mLoadingMore) {
            // Append the new items to the existing set.
            if (mCourseVotes == null) {
                mCourseVotes = courses;
            } else {
                for (CourseVote cv : courses) {
                    mCourseVotes.add(cv);
                }
            }
        } else {
            // Discard previous set, replace with new.
            mCourseVotes = courses;
        }

        notifyDataSetChanged();
        mLoadingMore = false;

        if (mListener != null) {
            // Notify the callback that the update finished.
            mListener.onTopCourseAdapterUpdateComplete(this);
        }
    }
    @Override
    public void onGetTopCoursesFailure(String errorMessage) {
        mCourseVotes = null;
        mLoadingMore = false;

        Log.e(TAG, "Failed to get top courses: " + errorMessage);

        if (mListener != null) {
            mListener.onTopCourseAdapterUpdateComplete(this);
        }
    }
}
