package com.lucky.loadmorerecyclerview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.lucky.loadmorerecyclerview.dao.ContentsDao;
import com.lucky.loadmorerecyclerview.model.ContentsListModel;
import com.lucky.loadmorerecyclerview.widget.LoadMoreRecyclerView;
import com.lucky.loadmorerecyclerview.widget.OnLoadMoreListener;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnLoadMoreListener {

    private LoadMoreRecyclerView recyclerView;
    private ContentsListAdapter adapter;

    private boolean isRetry = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        recyclerView = (LoadMoreRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLoadMoreListener(this);

        loadContentsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_linear:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                changeLayoutManager(linearLayoutManager);
                break;
            case R.id.action_grid:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
                changeLayoutManager(gridLayoutManager);
                break;
            case R.id.action_staggered:
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
                staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                changeLayoutManager(staggeredGridLayoutManager);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (adapter != null) {
            isRetry = false;
            adapter.clearItem();
            adapter.notifyDataSetChanged();
            loadContentsList();
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onLoadMore() {
        loadMoreContentsList();
    }

    private void loadContentsList() {

        ContentsDao.getContentsList()
                .subscribe(new Action1<ContentsListModel>() {
                    @Override
                    public void call(ContentsListModel contentsModels) {
                        updateContentsList(contentsModels);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void loadMoreContentsList() {
        ContentsDao.getContentsLoadMore(adapter.getItemCount(), isRetry)
                .subscribe(new Action1<ContentsListModel>() {
                    @Override
                    public void call(ContentsListModel contentsModels) {
                        recyclerView.onLoadMoreCompleted();
                        updateContentsList(contentsModels);
                        isRetry = false;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        recyclerView.onLoadMoreFailed();
                        isRetry = true;
                    }
                });
    }


    private void updateContentsList(@Nullable ContentsListModel contentsModels) {
        if (adapter == null) {
            adapter = new ContentsListAdapter(contentsModels);
            recyclerView.setAdapter(adapter);
        } else {
            int itemCount = adapter.getItemCount();
            adapter.addItems(contentsModels);
            if (contentsModels != null) {
                adapter.notifyItemRangeChanged(itemCount, contentsModels.size());
            }
        }
    }
}
