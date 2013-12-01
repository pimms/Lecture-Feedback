package chipmunk.unlimited.feedback;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import uk.co.senab.actionbarpulltorefresh.library.*;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Common superclass shared by FeedFragment,
 * StatisticsFragment and TodayFragment. Provides
 * a framework for safely updating the fragments.
 *
 * Subclasses must override "doRefresh()", and must
 * call "onFragmentInitialized()" after all init has
 * been performed.
 */
public abstract class UpdateableFragment extends ListFragment
        implements OnRefreshListener {
    private boolean mInitialized;
    private boolean mScheduledUpdate;

    private PullToRefreshLayout mPtrLayout;


    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onActivityCreated(bundle);
        onFragmentInitialized();

        ViewGroup viewGroup = (ViewGroup)view;
        mPtrLayout = new PullToRefreshLayout(getActivity());

        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list)
                .listener(this)
                .setup(mPtrLayout);
    }


    @Override
    public void onRefreshStarted(View view) {
        Log.d("YOLO", "YOOOLOOO");

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                mPtrLayout.setRefreshComplete();
            }
        }.execute();
    }


    protected void onFragmentInitialized() {
        mInitialized = true;

        if (mScheduledUpdate) {
            doRefresh();
            mScheduledUpdate = false;
        }
    }

    public final void refreshContents() {
        if (mInitialized) {
            doRefresh();
        } else {
            mScheduledUpdate = true;
        }
    }

    protected abstract void doRefresh();
}

