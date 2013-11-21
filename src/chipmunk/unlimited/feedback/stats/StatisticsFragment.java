package chipmunk.unlimited.feedback.stats;

import chipmunk.unlimited.feedback.MainActivityFragmentInterface;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.revfeed.FeedActivity;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class StatisticsFragment extends Fragment
            implements MainActivityFragmentInterface,
                       AdapterView.OnItemClickListener {
    private StatisticsAdapter mAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_statistics, container, false);

        mAdapter = new StatisticsAdapter(getActivity());
        ListView listView = (ListView)rootView.findViewById(R.id.stats_list_view);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

		return rootView;
	}

    @Override
    public void refreshContents() {
        mAdapter.reloadSubscriptions();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SubscriptionItem subItem = (SubscriptionItem)mAdapter.getItem(i);

        if (subItem != null) {
            Intent intent = new Intent(getActivity(), CourseLecturesActivity.class);
            startActivity(intent)
        }
    }
}
