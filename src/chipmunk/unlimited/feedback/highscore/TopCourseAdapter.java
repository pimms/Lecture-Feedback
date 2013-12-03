package chipmunk.unlimited.feedback.highscore;

import android.content.Context;
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
    private static final String TAG = "TopCourseAdapter";


    private LayoutInflater mInflater;
    private Context mContext;

    private List<CourseVote> mCourseVotes;
    private boolean mLoadingMore;


    public TopCourseAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reloadCourses();
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


    public void reloadCourses() {
        WebAPI webApi = new WebAPI();
        webApi.getTopCourses(this, 0, 25);
    }

    public void loadMoreCourses() {
        mLoadingMore = true;

        WebAPI webApi = new WebAPI();
        webApi.getTopCourses(this, mCourseVotes.size(), 25);
    }


    @Override
    public void onGetTopCoursesSuccess(List<CourseVote> courses) {
        if (mLoadingMore) {
            Log.d(TAG, "Loaded "+courses.size()+" more, dunno what to do");
            if (mCourseVotes == null) {
                mCourseVotes = courses;
            } else {
                for (CourseVote cv : courses) {
                    mCourseVotes.add(cv);
                }
            }
        } else {
            mCourseVotes = courses;
        }

        notifyDataSetChanged();
        mLoadingMore = false;
    }
    @Override
    public void onGetTopCoursesFailure(String errorMessage) {
        mCourseVotes = null;
        mLoadingMore = false;
    }
}
