package chipmunk.unlimited.feedback.highscore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import chipmunk.unlimited.feedback.R;

public class TopCourseAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;


    public TopCourseAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null || view.getId() != R.layout.list_item_course) {
            mInflater.inflate(R.layout.list_item_course, null);
        }



        return view;
    }
}
