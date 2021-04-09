package io.fuwafuwa.banjo.ui;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

import io.fuwafuwa.banjo.model.Size;

public class ThumbNailRecyclerAdapter extends DefaultRecyclerAdapter<ThumbNaiUnit> {

    private Size thumbSize;

    public ThumbNailRecyclerAdapter(Context context) {
        super(context);
    }

    public ThumbNailRecyclerAdapter(Context context, List<ThumbNaiUnit> objects) {
        super(context, objects);
    }

    @Override
    DefaultRecyclerViewHolder<ThumbNaiUnit> withCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThumbNailViewHolder(parent, thumbSize);
    }

    @Override
    public void onBindViewHolder(@NonNull DefaultRecyclerViewHolder<ThumbNaiUnit> holder, int position) {
        holder.update(getItem(position));
    }

    public void setThumbSize(Size thumbNailSize) {
        this.thumbSize = thumbNailSize;
    }
}