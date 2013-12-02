package chipmunk.unlimited.feedback.highscore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;
import chipmunk.unlimited.feedback.webapi.WebAPI;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;

public class TopCourseAdapter extends BaseAdapter
        implements GetTopCoursesCallback {
    private LayoutInflater mInflater;
    private Context mContext;

    private List<SubscriptionItem> mCourses;
    private List<CourseVote> mCourseVotes;


    public TopCourseAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reloadSubscriptions();
    }


    @Override
    public int getCount() {
        return mCourses.size();
    }
    @Override
    public Object getItem(int i) {
        return mCourses.get(i);
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

        SubscriptionItem sub = mCourses.get(i);
        CourseVote vote = getCourseVote(sub.getHigCode());

        tvName.setText(sub.getName());

        if (vote == null) {
            tvPos.setText("?");
            tvNeg.setText("?");
        } else {
            tvPos.setText("" + vote.getPositiveVoteCount());
            tvNeg.setText("" + vote.getNegativeVoteCount());
        }

        return view;
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
        mCourses = db.getSubscriptionList();

        WebAPI webApi = new WebAPI();
        webApi.getTopCourses(this, 0, 25);
    }


    @Override
    public void onGetTopCoursesSuccess(List<WebAPI.CourseVote> courses) {
        mCourseVotes = courses;
        notifyDataSetChanged();
    }
    @Override
    public void onGetTopCoursesFailure(String errorMessage) {
        mCourseVotes = null;
    }
}
