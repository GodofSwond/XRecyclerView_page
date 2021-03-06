package com.lonch.xrecyclerview_page;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

/**
 * Created by dongxuL on 18/5/7.
 */
public class XRecyclerView extends FrameLayout implements SwipyRefreshLayout.OnRefreshListener {
    // 是否有更多  这个值 需要与否自己看着办 在这里处理和在 发送请求后的到的数据为空的时候去处理也可以。一样的。
    // 这个值的目的就是为了让用户上拉加载在没有了数据了还去请求服务器
    public boolean haveMore = true;
    // 下拉刷新状态
    public final int PULL_TO_REFRESH = 0;
    // 加载更多状态
    public final int LOAD_MORE = 1;
    // 当前状态  保存当前的状态
    public int CURRENT_STATE = PULL_TO_REFRESH;
    // 是否正在加载 自动加载的时候这个值很有必要，如果自动加载这个值可以不用 SwipyRefreshLayout 这个框架已经做了。  如果你用其他框架 没有做这样的处理，需要加这个值，
    public boolean isLoad = false;
    public int countsPager = 1;

    private Context mContext;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private RecyclerView mRecyclerView;
    private XRecyclerViewListener mListener;

    private boolean mEnableAutoLoad = false;

    public XRecyclerView(Context context) {
        super(context);
        mContext = context;
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context, attrs);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.setClickable(true);
        this.setFocusable(true);

        View view = LayoutInflater.from(context).inflate(R.layout.xrececler_view, null);
        mSwipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipy_refreshlayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        mSwipyRefreshLayout.setOnRefreshListener(this);


        // 设置 自动加载的监听
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastCompleteVisibleItemIndex = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastCompleteVisibleItemIndex == totalItemCount - 1 && isSlidingToLast) {
                        if (mEnableAutoLoad && !isLoad && null != mListener) {
                            mSwipyRefreshLayout.setRefreshing(true);
                            mListener.onLoadMore();
                            isLoad = true;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0;
            }
        });

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.XRecyclerView);

        SwipyRefreshLayoutDirection direction = SwipyRefreshLayoutDirection.getFromInt(mTypedArray.getInt(R.styleable.XRecyclerView_direction, 0));
        if (direction == SwipyRefreshLayoutDirection.BOTH) {
            mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        } else if (direction != SwipyRefreshLayoutDirection.TOP) {
            mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        } else {
            mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addView(view, params);
        mTypedArray.recycle();
    }

    /**
     * 设置 LayoutManager 是listview 还是 Gridview 的样子
     *
     * @param layout
     */
    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        mRecyclerView.setLayoutManager(layout);
    }

    /**
     * 设置  刷新的模式
     *
     * @param direction
     */
    public void setDirection(SwipyRefreshLayoutDirection direction) {
        mSwipyRefreshLayout.setDirection(direction);
    }

    /**
     * 是否  开启 自动加载
     *
     * @param enable
     */
    public void setAutoLoadEnable(boolean enable) {
        mEnableAutoLoad = enable;
    }

    public void stopRefreshAndLoadMore() {
        mSwipyRefreshLayout.setRefreshing(false);
        isLoad = false;
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setXRecyclerViewListener(XRecyclerViewListener listener) {
        mListener = listener;
    }

    /**
     * 设置 常用的监听器 一般不是很负载的 列表用这个就可以了
     *
     * @param mOnNeedPostRequestListener
     */
    public void setSimpleXRecyclerViewListener(OnNeedPostRequestListener mOnNeedPostRequestListener) {
        SimpleXRecyclerViewListener mSimpleXRecyclerViewListener = new SimpleXRecyclerViewListener(mOnNeedPostRequestListener);
        setXRecyclerViewListener(mSimpleXRecyclerViewListener);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            if (null != mListener) {
                mListener.onRefresh();
            }
        } else {
            if (!isLoad && null != mListener) {
                mListener.onLoadMore();
            }
        }
    }

    public interface XRecyclerViewListener {
        public void onRefresh();

        public void onLoadMore();
    }

    public interface OnNeedPostRequestListener {
        void postRequest();
    }


    private class SimpleXRecyclerViewListener implements XRecyclerViewListener {
        private OnNeedPostRequestListener mOnNeedPostRequestListener;

        public SimpleXRecyclerViewListener(OnNeedPostRequestListener mOnNeedPostRequestListener) {
            this.mOnNeedPostRequestListener = mOnNeedPostRequestListener;
        }

        @Override
        public void onRefresh() {
            isLoad = true;
            CURRENT_STATE = PULL_TO_REFRESH;
            countsPager = 1;
            if (mOnNeedPostRequestListener != null) {
                mOnNeedPostRequestListener.postRequest();
            }
        }

        @Override
        public void onLoadMore() {
            if (isLoad)
                return;
            if (haveMore) {
                isLoad = true;
                CURRENT_STATE = LOAD_MORE;
                countsPager += 1;
                Log.i("*****TAG----", "countsPager: " + countsPager);

                if (mOnNeedPostRequestListener != null) {
                    mOnNeedPostRequestListener.postRequest();
                }
            } else {
                //加载更多结束
                stopRefreshAndLoadMore();
            }
        }
    }


}
