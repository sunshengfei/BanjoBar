package io.fuwafuwa.banjo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.SoftReference;


/**
 * Created by fred on 2016/11/2.
 */

public class DefaultRecyclerViewHolder<M> extends RecyclerView.ViewHolder {

    public M item;
    protected SoftReference<Context> mContextRef;

    public DefaultRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    protected <V extends View> V $(int id) {
        return itemView.findViewById(id);
    }

    // this will be Deprecated for inflating view by id
    @Deprecated
    public DefaultRecyclerViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
        mContextRef = new SoftReference<>(getContext());
    }


    public void update(M data) {

    }


}
