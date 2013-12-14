package chipmunk.unlimited.feedback.highscore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
    private Activity mActivity;
    private OnTopCourseAdapterUpdateListener mListener;
    private List<CourseVote> mCourseVotes;
    private boolean mLoadingMore;


    public TopCourseAdapter(Activity activity) {
        mActivity = activity;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        return 1;
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
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int i) {
        return mCourseVotes != null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (mCourseVotes == null) {
            return getLoadingView(view);
        }

        return getCourseView(view, i);
    }

    private View getCourseView(View view, int i) {
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

    private View getLoadingView(View view) {
        if (view == null || view.getId() != R.layout.progressbar_view) {
            view = mInflater.inflate(R.layout.progressbar_view, null);
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

        displayNoInternetDialog(errorMessage);
    }


    private void displayNoInternetDialog(String errMsg) {
        new AlertDialog.Builder(mActivity)
            .setTitle(mActivity.getString(R.string.no_internet_dialog_title))
            .setMessage(mActivity.getString(R.string.no_internet_dialog_message)
                    + "\n\nError message:\n" + errMsg)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int i) {
                    mActivity.finish();
                }
            }).show();
    }
}
