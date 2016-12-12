package com.lucky.loadmorerecyclerview.model;

import java.util.ArrayList;

public class ContentsListModel extends ArrayList<ContentsModel> {
    private boolean hasMore;

    public boolean hasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
