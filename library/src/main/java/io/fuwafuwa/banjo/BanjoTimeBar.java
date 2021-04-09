package io.fuwafuwa.banjo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.fuwafuwa.banjo.extension.VideoThumbNailExtractor;
import io.fuwafuwa.banjo.model.Size;
import io.fuwafuwa.banjo.ui.ThumbNaiUnit;


public class BanjoTimeBar extends SelectionBar implements VideoThumbNailExtractor.Callback {

    private Handler handler;

    private Thread singleTask;

    private List<ThumbNaiUnit> dataSets;
    private VideoThumbNailExtractor extractor;
    private boolean autoTrashLocalImages;

    private final LinkedList<String> localImages = new LinkedList<>();

    public BanjoTimeBar(@NonNull Context context) {
        super(context);
    }

    public BanjoTimeBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BanjoTimeBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private Handler.Callback callBack;
    private Handler.Callback mRefCallBack;

    @Override
    protected void initLayout() {
        super.initLayout();
        dataSets = new ArrayList<>();
        mRefCallBack = msg -> {
            if (msg.what == 0) {
                dataSet(dataSets);
            } else if (msg.what == 1) {
                notifyDataChanged(msg.arg1);
            }
            return false;
        };
        callBack = new HandlerCallBack<>(mRefCallBack);
        handler = new Handler(callBack);
    }

    public void attachVideoSource(String dataSource, String outputDir, long spanMills, Size frameSize) {
        if (singleTask != null) {
            if (singleTask.isAlive()) {
                singleTask.interrupt();
            }
            singleTask = null;
        }
        singleTask = new Thread(() -> {
            extractor = new VideoThumbNailExtractor(outputDir, settings.preferThumbExtractor);
            extractor.extractFrames(
                    dataSource,
                    spanMills,
                    frameSize,
                    this
            );
        });
        singleTask.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
        if (singleTask != null) {
            if (singleTask.isAlive()) {
                singleTask.interrupt();
            }
            singleTask = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        autoTrash();
    }

    private void autoTrash() {
        if (autoTrashLocalImages) {
            synchronized (localImages) {
                String imagesUrl = localImages.pop();
                File file = new File(imagesUrl);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onSkeletonPrepared(List<Long> thumbsSlot) {
        dataSets.clear();
        for (int i = 0; i < thumbsSlot.size(); i++) {
            ThumbNaiUnit unit = new ThumbNaiUnit();
            unit.setTimePoint(thumbsSlot.get(i));
            dataSets.add(unit);
        }
        if (handler != null)
            handler.sendEmptyMessage(0);
    }

    public void setAutoTrashLocalImages(boolean enable) {
        this.autoTrashLocalImages = enable;
    }

    public void release() {
        if (extractor != null) {
            extractor.release();
        }
    }

    @Override
    public void onThumbReceived(int i, String urlLocation) {
        ThumbNaiUnit unit = dataSets.get(i);
        unit.setThumbUrl(urlLocation);
        synchronized (localImages) {
            localImages.add(urlLocation);
        }
        if (handler != null)
            Message.obtain(handler, 1, i, 0).sendToTarget();
    }


    static class HandlerCallBack<T extends Handler.Callback> implements Handler.Callback {

        private final WeakReference<T> mRef;

        HandlerCallBack(T t) {
            this.mRef = new WeakReference<>(t);
        }

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (mRef != null) {
                T t = mRef.get();
                if (t != null)
                    return t.handleMessage(msg);
            }
            return false;
        }
    }

}
