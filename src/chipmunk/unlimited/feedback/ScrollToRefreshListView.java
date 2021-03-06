package chipmunk.unlimited.feedback;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;


public class ScrollToRefreshListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = "ScrollToLoadListView";

    public interface OnScrollToRefreshListener {
        public void onScrollToLoad(ScrollToRefreshListView view);
    }

    private int mScrollState;
    private OnScrollToRefreshListener mListener;
    private boolean mIsRefreshing;
    private boolean mAtBottom;


    public ScrollToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollToRefreshListView(Context context) {
        super(context);
    }


    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    private void init() {
        // I am a strong, confident woman. I talk to myself. I listen
        // to myself.
        this.setOnScrollListener(this);
    }

    public void setOnScrollToRefreshListener(OnScrollToRefreshListener listener) {
        mListener = listener;
    }


    private void onRefreshBegin() {
        mIsRefreshing = true;

        if (mListener != null) {
            mListener.onScrollToLoad(this);
        }
    }

    public void onRefreshComplete() {
        mIsRefreshing = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int state) {
        if (absListView == this) {
            mScrollState = state;
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view == this) {
            boolean wasAtBottom = mAtBottom;
            mAtBottom = (firstVisibleItem + visibleItemCount == totalItemCount);

            if (!mIsRefreshing && !wasAtBottom && mAtBottom) {
                if (mScrollState != SCROLL_STATE_IDLE) {
                    if (mListener != null) {
                        onRefreshBegin();
                    }
                }
            }
        }
    }
}
