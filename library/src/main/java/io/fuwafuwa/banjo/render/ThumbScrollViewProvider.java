package io.fuwafuwa.banjo.render;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.SoftReference;
import java.util.List;

import io.fuwafuwa.banjo.IThumbSizeProvider;
import io.fuwafuwa.banjo.model.Size;
import io.fuwafuwa.banjo.ui.ThumbNaiUnit;
import io.fuwafuwa.banjo.ui.ThumbNailRecyclerAdapter;

public class ThumbScrollViewProvider implements IThumbSizeProvider<RecyclerView, ThumbNaiUnit> {

    private SoftReference<Context> mContextRef;
    private List<ThumbNaiUnit> dataSets;
    private ThumbNailRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private ViewTreeObserver.OnGlobalLayoutListener vic;
    private Size thumbNailSize;


    public ThumbScrollViewProvider(Context context) {
        this.mContextRef = new SoftReference<>(context);
    }

    @Override
    public RecyclerView provideView() {
        if (recyclerView != null) return recyclerView;
        Context context = mContextRef.get();
        recyclerView = new RecyclerView(context);
        recyclerView.setId(View.generateViewId());
        LinearLayoutManager horManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(horManager);
        adapter = new ThumbNailRecyclerAdapter(context, dataSets);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
//        ViewTreeObserver vt = recyclerView.getViewTreeObserver();
//        vic = () -> contentLength = recyclerView.computeHorizontalScrollRange();
        return recyclerView;
    }

    @Override
    public void setDataSet(List<ThumbNaiUnit> dataSets) {
        adapter.notify(dataSets);
        this.dataSets = adapter.getDataSets();
    }

    @Override
    public void notifyDataChanged(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public int getContentLength() {
        if (adapter == null || adapter.getItemCount() == 0)
            return 0;
        int range = 0;
        if (recyclerView != null) {
            range = recyclerView.computeHorizontalScrollRange();
        }
        if (range != 0) return range;
        //evaluate
        if (thumbNailSize != null) {
            return thumbNailSize.getWidth() * adapter.getItemCount();
        }
        return range;
    }


    @Override
    public void setThumbSize(Size thumbNailSize) {
        this.thumbNailSize = thumbNailSize;
        adapter.setThumbSize(thumbNailSize);
        adapter.notifyDataSetChanged();
    }
}
