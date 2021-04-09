package io.fuwafuwa.banjo.ui;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultRecyclerAdapter<T> extends RecyclerView.Adapter<DefaultRecyclerViewHolder<T>> {

    protected Context mContext;
    public int mFocusIndex = -1;

    public void setDataSets(List<T> dataSets) {
        this.dataSets = dataSets;
    }

    private List<T> dataSets;

    public T getItem(int position) {
        if (dataSets == null) {
            return null;
        }
        if (position < 0) return null;
        return dataSets.get(position);
    }


    public DefaultRecyclerAdapter(Context context) {
        init(context, new ArrayList<T>());
    }

    public DefaultRecyclerAdapter(Context context, List<T> objects) {
        init(context, objects);
    }

    protected void init(Context context, List<T> objects) {
        mContext = context;
        dataSets = objects;
    }

    @Override
    public DefaultRecyclerViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        return withCreateViewHolder(parent, viewType);
    }

    abstract DefaultRecyclerViewHolder<T> withCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull DefaultRecyclerViewHolder<T> holder, int position) {
    }

    @Override
    public int getItemCount() {
        return dataSets == null ? 0 : dataSets.size();
    }


    public List<T> getDataSets() {
        if (dataSets == null) {
            dataSets = new ArrayList<>();
        }
        return dataSets;
    }

    public void notify(List<T> datas) {
        if (dataSets != null) {
            dataSets.clear();
        } else {
            dataSets = new ArrayList<>();
        }
        if (datas != null && datas.size() > 0) {
            dataSets.addAll(datas);
        }
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

}