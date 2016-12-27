package com.lucky.loadmorerecyclerview.dao;

import com.lucky.loadmorerecyclerview.model.ContentsListModel;
import com.lucky.loadmorerecyclerview.model.ContentsModel;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class ContentsDao {
    private static final int LIST_SIZE = 15;

    public static Observable<ContentsListModel> getContentsList() {

        return Observable
                .create(new Observable.OnSubscribe<ContentsListModel>() {
                    @Override
                    public void call(Subscriber<? super ContentsListModel> subscriber) {
                        ContentsListModel contentsModels = new ContentsListModel();
                        for (int i = 0; i < LIST_SIZE; i++) {
                            contentsModels.add(new ContentsModel("title" + i, "contents" + i, "http://www.kccosd.org/files/testing_image.jpg"));
                        }
                        contentsModels.setHasMore(true);

                        subscriber.onNext(contentsModels);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ContentsListModel> getContentsLoadMore(final int size, boolean isRetry) {

        if (!isRetry && size % (LIST_SIZE * 2) == 0) {
            return getContentsLoadMoreThrowError();
        }
        return Observable
                .create(new Observable.OnSubscribe<ContentsListModel>() {
                    @Override
                    public void call(Subscriber<? super ContentsListModel> subscriber) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ContentsListModel contentsModels = new ContentsListModel();
                        for (int i = size; i < size + LIST_SIZE; i++) {
                            contentsModels.add(new ContentsModel("title" + i, "contents loadMore" + i, "http://www.kccosd.org/files/testing_image.jpg"));
                        }
                        contentsModels.setHasMore(true);

                        subscriber.onNext(contentsModels);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ContentsListModel> getContentsLoadMoreThrowError() {

        return Observable
                .create(new Observable.OnSubscribe<ContentsListModel>() {
                    @Override
                    public void call(Subscriber<? super ContentsListModel> subscriber) {

                        subscriber.onError(new RuntimeException("error"));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
