package com.lucky.loadmorerecyclerview;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucky.loadmorerecyclerview.model.ContentsListModel;
import com.lucky.loadmorerecyclerview.model.ContentsModel;
import com.lucky.loadmorerecyclerview.widget.LoadMoreRecyclerView;

public class ContentsListAdapter extends RecyclerView.Adapter<ContentsListAdapter.ContentsViewHolder> implements LoadMoreRecyclerView.LoadMoreInterface {

    @Nullable
    private ContentsListModel contentsModels;

    public ContentsListAdapter(@Nullable ContentsListModel contentsModels) {
        this.contentsModels = contentsModels;
    }

    public void addItems(ContentsListModel contentsModels) {
        if (contentsModels == null) {
            return;
        }

        if (this.contentsModels == null) {
            this.contentsModels = new ContentsListModel();
        }
        this.contentsModels.addAll(contentsModels);
        this.contentsModels.setHasMore(contentsModels.hasMore());
    }

    public void clearItem() {
        if (this.contentsModels != null) {
            this.contentsModels.clear();
            this.contentsModels.setHasMore(false);
        }
    }

    @Override
    public ContentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentsViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ContentsViewHolder holder, int position) {
        if (contentsModels == null) {
            return;
        }
        holder.bind(contentsModels.get(position), position);
    }

    @Override
    public int getItemCount() {
        return contentsModels == null ? 0 : contentsModels.size();
    }

    @Override
    public boolean hasMore() {
        return contentsModels != null && contentsModels.hasMore();
    }

    class ContentsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView cardImageView;
        private final TextView titleTextView;
        private final TextView contentsTextView;

        ContentsViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_layout, parent, false));
            cardImageView = (ImageView) itemView.findViewById(R.id.card_image);
            titleTextView = (TextView) itemView.findViewById(R.id.card_title);
            contentsTextView = (TextView) itemView.findViewById(R.id.card_contents);
        }

        void bind(ContentsModel contentsModel, int position) {
            titleTextView.setText(contentsModel.getTitle());
            contentsTextView.setText(contentsModel.getContents());

            try {
                int index = position % 10;
                int drawableResId = itemView.getContext().getResources().getIdentifier("image_" + index, "drawable", itemView.getContext().getPackageName());
                cardImageView.setImageResource(drawableResId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
