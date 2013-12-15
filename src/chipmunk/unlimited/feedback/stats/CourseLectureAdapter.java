package chipmunk.unlimited.feedback.stats;


import android.content.Context;
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

public class CourseLectureAdapter extends BaseAdapter implements GetLectureVotesAllCallback {
    private static final String TAG = "CourseLectureAdapter";

    private boolean mNoLectures;
    private List<LectureVote> mVotes;
    private LayoutInflater mInflater;
    private String mCourse;
    private Context mContext;

    public CourseLectureAdapter(Context context, String course) {
        mInflater = (LayoutInflater)context.getSystemService(
                                    Context.LAYOUT_INFLATER_SERVICE);
        mCourse = course;
        mContext = context;
    }

    public void reloadItems(int first, int count) {
        WebAPI webApi = new WebAPI();
        webApi.getLectureVotesAll(this, mCourse, first, count);
    }


    @Override
    public int getCount() {
        if (mNoLectures) {
            return 1;
        }

        if (mVotes != null) {
            return mVotes.size();
        }

        return 1;
    }
    @Override
    public Object getItem(int i) {
        if (!mNoLectures && mVotes != null) {
            return mVotes.get(i);
        }

        return null;
    }
    @Override
    public long getItemId(int i) {
        if (mVotes != null) {
            return i+1;
        }

        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (mVotes == null && !mNoLectures) {
            return getLoadingView(view);
        }

        if (!mNoLectures) {
            return getLectureView(view, i);
        } else {
            return getNoLecturesView(view);
        }
    }

    private View getLoadingView(View view) {
        if (view == null || view.getId() != R.layout.progressbar_view) {
            view = mInflater.inflate(R.layout.progressbar_view, null);
        }

        return view;
    }

    private View getLectureView(View view, int position) {
        if (view == null || view.getId() != R.layout.list_item_lecture_total) {
            view = mInflater.inflate(R.layout.list_item_lecture_total, null);
        }

        TextView tvDate = (TextView)view.findViewById(R.id.lecture_total_text_view_date);
        TextView tvTime = (TextView)view.findViewById(R.id.lecture_total_text_view_time);
        TextView tvLect = (TextView)view.findViewById(R.id.lecture_total_text_view_lecturer);
        TextView tvNeg  = (TextView)view.findViewById(R.id.simple_thumb_text_view_negative);
        TextView tvPos  = (TextView)view.findViewById(R.id.simple_thumb_text_view_positive);

        LectureVote vote = mVotes.get(position);
        tvDate.setText(vote.getLectureItem().getPrettyDateString(mContext));
        tvTime.setText(vote.getLectureItem().getTimeString());
        tvLect.setText(vote.getLectureItem().getLecturer());
        tvPos.setText("" + vote.getPositiveVoteCount());
        tvNeg.setText("" + vote.getNegativeVoteCount());

        return view;
    }

    private View getNoLecturesView(View view) {
        if (view == null || view.getId() != R.layout.tutorial) {
            view = mInflater.inflate(R.layout.tutorial, null);
        }

        TextView tvTitle = (TextView)view.findViewById(R.id.tutorial_text_view_title);
        TextView tvDesc = (TextView)view.findViewById(R.id.tutorial_text_view_desc);

        tvTitle.setText(mContext.getString(R.string.activity_courselectures_no_lec_title));
        tvDesc.setText(mContext.getString(R.string.activity_courselectures_no_lec_desc));

        return view;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int i) {
        return !mNoLectures && (mVotes != null);
    }




    @Override
    public void onGetLectureVotesAllSuccess(List<WebAPI.LectureVote> items) {
        mVotes = items;

        if (mVotes == null || mVotes.size() == 0) {
            mNoLectures = true;
        }

        notifyDataSetChanged();
    }
    @Override
    public void onGetLectureVotesAllFailure(String errorMessage) {
        mVotes = null;
        mNoLectures = true;
        notifyDataSetChanged();
    }
}
