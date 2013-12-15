package chipmunk.unlimited.feedback.stats;

import chipmunk.unlimited.feedback.UpdateableFragment;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class StatisticsFragment extends UpdateableFragment
            implements  AdapterView.OnItemClickListener,
                        StatisticsAdapter.StatisticsAdapterUpdateListener {
    private StatisticsAdapter mAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_statistics, container, false);
		return rootView;
	}
    @Override
    public void onViewCreated(View view, Bundle bundle) {
        cancelScheduledRefresh();
        super.onViewCreated(view, bundle);

        mAdapter = new StatisticsAdapter(getActivity());
        mAdapter.setStatisticsAdapterUpdateListener(this);

        ListView listView = getListView();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        refreshContents();
    }
    @Override
    public void doRefresh() {
        mAdapter.reloadCourses();
    }
    @Override
    public void onStatsAdapterUpdated(StatisticsAdapter adapter, boolean success) {
        onUpdateCompleted();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SubscriptionItem subItem = (SubscriptionItem)mAdapter.getItem(i);

        if (subItem != null) {
            Intent intent = new Intent(getActivity(), CourseLecturesActivity.class);
            intent.putExtra(CourseLecturesActivity.PARAM_COURSE_CODE, subItem.getHigCode());
            intent.putExtra(CourseLecturesActivity.PARAM_COURSE_NAME, subItem.getName());
            startActivity(intent);
        }
    }
}
