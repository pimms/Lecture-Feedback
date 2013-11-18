package chipmunk.unlimited.feedback.stats;

import chipmunk.unlimited.feedback.MainActivityFragmentInterface;
import chipmunk.unlimited.feedback.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StatisticsFragment extends Fragment
            implements MainActivityFragmentInterface {
    private StatisticsAdapter mAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_statistics, container, false);

        mAdapter = new StatisticsAdapter(getActivity());
        ListView listView = (ListView)rootView.findViewById(R.id.stats_list_view);
        listView.setAdapter(mAdapter);

		return rootView;
	}

    @Override
    public void refreshContents() {
        mAdapter.reloadSubscriptions();
    }
}
