package com.lucky.loadmorerecyclerview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucky.loadmorerecyclerview.R;

public class LoadMoreRecyclerView extends RecyclerView {
    private static final int DEFAULT_HEIGHT = 50;
    private static final int DEFAULT_THRESHOLD = 10;

    @Nullable
    private OnLoadMoreListener onLoadMoreListener;

    @Nullable
    private LoadMoreRecyclerViewAdapter loadMoreAdapter;

    private boolean isLoadMoreLoading;
    private boolean isLoadMoreFailed;

    @LayoutRes
    private int layoutResId;
    @IdRes
    private int loadingViewId;
    @IdRes
    private int failedViewId;

    private int loadMoreHeight;

    private int threshold;

    public LoadMoreRecyclerView(Context context) {
        this(context, null, -1);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(@Nullable AttributeSet attrs, int defStyle) {

        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = findLastVisibleItemPosition();

                int itemCount = getLayoutManager().getItemCount();
                if (!isLoadMoreLoading && itemCount < (lastVisibleItemPosition + threshold)) {
                    notifyLoadMore();
                }
            }
        });


        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LoadMoreRecyclerView, defStyle, 0);

        loadMoreHeight = typedArray.getDimensionPixelSize(R.styleable.LoadMoreRecyclerView_loadmore_height, getDefaultHeight());
        layoutResId = typedArray.getResourceId(R.styleable.LoadMoreRecyclerView_loadmore_layout, R.layout.default_load_more_progress_layout);
        loadingViewId = typedArray.getResourceId(R.styleable.LoadMoreRecyclerView_loadmore_loading_view_id, R.id.load_more_progress_bar);
        failedViewId = typedArray.getResourceId(R.styleable.LoadMoreRecyclerView_loadmore_failed_view_id, R.id.load_more_fail_layout);
        threshold = typedArray.getInteger(R.styleable.LoadMoreRecyclerView_loadmore_threshold, DEFAULT_THRESHOLD);
        typedArray.recycle();
    }

    private int getDefaultHeight() {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (DEFAULT_HEIGHT * density + 0.5f);
    }

    private void notifyLoadMore() {
        isLoadMoreFailed = false;
        isLoadMoreLoading = true;
        if (onLoadMoreListener != null) {
            onLoadMoreListener.onLoadMore();
        }
    }

    private int findLastVisibleItemPosition() {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
            return manager.findLastVisibleItemPosition();

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastVisibleItemPositions = manager.findLastVisibleItemPositions(null);
            int lastPosition = 0;
            for (int lastVisibleItemPosition : lastVisibleItemPositions) {
                lastPosition = Math.max(lastPosition, lastVisibleItemPosition);
            }
            return lastPosition;
        }
        return 0;
    }

    private void notifyLoadMoreViewInvalid() {
        if (loadMoreAdapter == null || !loadMoreAdapter.hasMore()) {
            return;
        }

        loadMoreAdapter.notifyItemChanged(loadMoreAdapter.getItemCount() - 1);
    }

    public void setLoadMoreHeight(int loadMoreHeight) {
        this.loadMoreHeight = loadMoreHeight;
        notifyLoadMoreViewInvalid();
    }

    public void setLayoutResId(@LayoutRes int layoutResId) {
        this.layoutResId = layoutResId;
        notifyLoadMoreViewInvalid();
    }

    public void setLoadingViewId(@IdRes int loadingViewId) {
        this.loadingViewId = loadingViewId;
        notifyLoadMoreViewInvalid();
    }

    public void setFailedViewId(@IdRes int failedViewId) {
        this.failedViewId = failedViewId;
        notifyLoadMoreViewInvalid();
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        notifyLoadMoreViewInvalid();
    }

    public boolean isLoadMoreLoading() {
        return isLoadMoreLoading;
    }

    /**
     *
     */
    public void onLoadMoreCompleted() {
        isLoadMoreLoading = false;
        notifyLoadMoreViewInvalid();
    }

    public void onLoadMoreFailed() {
        if (loadMoreAdapter != null && loadMoreAdapter.hasMore()) {
            isLoadMoreFailed = true;
            notifyLoadMoreViewInvalid();
        }
    }

    public void setLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    @Override
    public void setAdapter(Adapter adapter) {
        loadMoreAdapter = new LoadMoreRecyclerViewAdapter(adapter);
        super.setAdapter(loadMoreAdapter);
    }

    @Override
    public Adapter getAdapter() {
        assert loadMoreAdapter != null;
        return loadMoreAdapter.getAdapter();
    }

    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager manager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = manager.getSpanSizeLookup();

            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (loadMoreAdapter != null
                            && loadMoreAdapter.hasMore()
                            && position == loadMoreAdapter.getItemCount() - 1) {
                        return manager.getSpanCount();
                    }
                    return spanSizeLookup.getSpanSize(position);
                }
            });
        }
        super.setLayoutManager(layoutManager);
    }

    private class LoadMoreRecyclerViewAdapter extends RecyclerView.Adapter {
        private static final int VIEW_TYPE_LOAD_MORE = Integer.MAX_VALUE - 1;

        @NonNull
        private Adapter adapter;

        LoadMoreRecyclerViewAdapter(@NonNull Adapter adapter) {
            this.adapter = adapter;
        }


        @NonNull
        public Adapter getAdapter() {
            return adapter;
        }

        @Override
        public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_LOAD_MORE:
                    View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
                    ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                    if (layoutParams == null) {
                        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, loadMoreHeight);
                    } else {
                        layoutParams.height = loadMoreHeight;
                    }
                    itemView.setLayoutParams(layoutParams);
                    return new LoadMoreViewHolder(itemView);
                default:
                    return adapter.onCreateViewHolder(parent, viewType);
            }
        }

        @SuppressWarnings("all")
        @Override
        public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_LOAD_MORE:
                    bindLoadMoreViewHolder((LoadMoreViewHolder) holder);
                    break;
                default:
                    adapter.onBindViewHolder(holder, position);
                    break;
            }
        }

        private void bindLoadMoreViewHolder(LoadMoreViewHolder holder) {
            final LoadMoreViewHolder loadMoreViewHolder = holder;
            loadMoreViewHolder.retryView.setVisibility(isLoadMoreFailed ? View.VISIBLE : View.GONE);
            loadMoreViewHolder.loadingView.setVisibility(isLoadMoreFailed ? View.GONE : View.VISIBLE);

            if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }
            loadMoreViewHolder.retryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMoreViewHolder.retryView.setVisibility(View.GONE);
                    loadMoreViewHolder.loadingView.setVisibility(View.VISIBLE);

                    notifyLoadMore();
                }
            });
        }

        @Override
        public final int getItemCount() {
            return hasMore() ? adapter.getItemCount() + 1 : adapter.getItemCount();
        }

        @Override
        public final int getItemViewType(int position) {

            if (hasMore() && isLastPosition(position)) {
                return VIEW_TYPE_LOAD_MORE;
            }
            return adapter.getItemViewType(position);
        }

        @Override
        public final long getItemId(int position) {
            if (hasMore() && isLastPosition(position)) {
                return View.NO_ID;
            }
            return adapter.getItemId(position);
        }

        private boolean isLastPosition(int position) {
            return position == getItemCount() - 1;
        }

        private boolean hasMore() {
            if (adapter instanceof LoadMoreInterface) {
                LoadMoreInterface loadMoreInterface = (LoadMoreInterface) this.adapter;
                return loadMoreInterface.hasMore();
            }
            return false;
        }

        private class LoadMoreViewHolder extends RecyclerView.ViewHolder {

            private final View loadingView;
            private final View retryView;

            private LoadMoreViewHolder(View itemView) {
                super(itemView);

                loadingView = itemView.findViewById(loadingViewId);
                retryView = itemView.findViewById(failedViewId);
            }
        }

    }

    public interface LoadMoreInterface {
        boolean hasMore();
    }

}
