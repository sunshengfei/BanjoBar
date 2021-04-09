package io.fuwafuwa.banjo.render;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import io.fuwafuwa.banjo.IThumbHandlerProvider;
import io.fuwafuwa.banjo.IThumbTrackProvider;
import io.fuwafuwa.banjo.OnThumbActionListener;
import io.fuwafuwa.banjo.R;
import io.fuwafuwa.banjo.extension.T;
import io.fuwafuwa.banjo.ThumbDefaultGlobalSettings;
import io.fuwafuwa.banjo.ThumbGroupAccessNode;
import io.fuwafuwa.banjo.TrackEventDispatcher;
import io.fuwafuwa.banjo.model.Segment;
import io.fuwafuwa.banjo.profile.BarJustify;
import io.fuwafuwa.banjo.profile.SegmentConfig;
import io.fuwafuwa.banjo.profile.TrackConfig;
import io.fuwafuwa.banjo.profile.UnionMovingTransition;

public class ThumbTrackProvider implements IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout>, ThumbGroupAccessNode<View>, View.OnTouchListener {

    private UnionMovingTransition moving;
    private SoftReference<Context> mContextRef;
    private FrameLayout handlerView;
    private List<IThumbHandlerProvider<View>> thumbs;
    private List<IThumbHandlerProvider<View>> internals;
    private List<Segment> segmentList;

    private OnThumbActionListener mThumbActionListener;
    private ThumbDefaultGlobalSettings.Config settings;
    private GestureDetector mGestureDetector;
    private TrackEventDispatcher mTrackEventDispatcher;

    public final static int ChildTagKey = R.id.segment_ui_id;

    private int currentClickChildIndex = -1;

    private boolean hasSetup = false;

    public void setThumbActionListener(OnThumbActionListener mThumbActionListener) {
        this.mThumbActionListener = mThumbActionListener;
    }

    private String groupId;

    private TrackConfig trackConfig;

    private ThumbTrackProvider(Context context) {
        this(context, null, null, null);
    }

    public ThumbTrackProvider(Context context, TrackConfig trackConfig, OnThumbActionListener thumbActionListener, TrackEventDispatcher mTrackEventDispatcher) {
        if (groupId == null) {
            groupId = UUID.randomUUID().toString();
        }
        if (trackConfig == null)
            this.trackConfig = new TrackConfig();
        else this.trackConfig = trackConfig;
        thumbs = new LinkedList<>();
        internals = new LinkedList<>();
        segmentList = new LinkedList<>();
        this.mContextRef = new SoftReference<>(context);
        if (this.moving == null) this.moving = new UnionMovingTransition();
        this.mThumbActionListener = thumbActionListener;
        this.mTrackEventDispatcher = mTrackEventDispatcher;
    }

    @Override
    public void applyTrackConfig(TrackConfig trackConfig) {
        if (trackConfig != null) {
            this.trackConfig = trackConfig;
            setTrackBackground(this.trackConfig.trackBackground);
        } else {
            setTrackBackground(null);
        }

    }

    public TrackEventDispatcher getTrackEventDispatcher() {
        return mTrackEventDispatcher;
    }

    @Override
    public String groupId() {
        return groupId;
    }

    @Override
    public void notifyGlobalSetting(ThumbDefaultGlobalSettings.Config config) {
        this.settings = config;
    }

    @Override
    public ThumbDefaultGlobalSettings.Config getGlobalSetting() {
        return settings;
    }


    @Override
    public Collection<IThumbHandlerProvider<View>> provideViews() {
        return thumbs;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public FrameLayout provideView() {
        if (handlerView != null) return handlerView;
        Context context = mContextRef.get();
        handlerView = new FrameLayout(context);
        handlerView.setId(View.generateViewId());
        handlerView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (moving != null) {
                    postReloadChild(parent, child, thumbs, false);
                    postReloadChild(parent, child, internals, true);
                    applyTrackConfig(trackConfig);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                for (IThumbHandlerProvider<View> thumbView : thumbs) {
                    if (thumbView != null && thumbView.provideView() == child) {
                        if (mThumbActionListener != null) {
                            mThumbActionListener.onSegmentRemoved(thumbView);
                        }
                    }
                }
            }
        });
        if (mGestureDetector == null) {
            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (currentClickChildIndex != -1) {
                        //child 点击
                        IThumbHandlerProvider<View> ncm = thumbs.get(currentClickChildIndex);
                        if (ncm != null) {
                            whenSelected(currentClickChildIndex);
                        }
                        currentClickChildIndex = -1;
                    }
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    T.e("🐬", "onScroll");
                    if (mTrackEventDispatcher != null) {
                        return mTrackEventDispatcher.onDispatchScroll(groupId, e1, e2, distanceX, distanceY);
                    }
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    T.e("🐬", "onFling");
                    if (mTrackEventDispatcher != null) {
                        return mTrackEventDispatcher.onDispatchFling(groupId, e1, e2, velocityX, velocityY);
                    }
                    return false;
                }
            };
            this.mGestureDetector = new GestureDetector(mContextRef.get(), listener, null);
        }
        handlerView.setOnTouchListener(this);
        handlerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                //使用Handler替代
                internalSetup();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
            }
        });
        return handlerView;
    }

    private void postReloadChild(View parent, View child, @NonNull List<IThumbHandlerProvider<View>> thumbs, boolean isSystem) {
        for (IThumbHandlerProvider<View> thumbView : thumbs) {
            if (thumbView != null && thumbView.provideView() == child) {
                SegmentConfig sConfig = thumbView.getSegmentConfig();
                sConfig.contentLength = moving.contentLength;
                sConfig.totalScrollX = moving.totalScrollX;
                sConfig.centerAnchorX = moving.centerAnchorX;
                thumbView.applyConfig(sConfig);
                if (!isSystem && mThumbActionListener != null) {
                    mThumbActionListener.onSegmentAdded(thumbView);
                }
                parent.postDelayed(() -> {
                    child.setVisibility(View.VISIBLE);
                }, 180);
            }
        }
    }

    private void internalSetup() {
        if (hasSetup) return;
        internals.clear();
        SegmentConfig config = new SegmentConfig();
        config.justify = BarJustify.FIXED;
        config.maskColor = Color.TRANSPARENT;
        IThumbHandlerProvider<View> thumbView = makeComponentProvider(config);
        View childView = makeComponent(null, thumbView);
        childView.setMinimumWidth(100);
        childView.setVisibility(View.INVISIBLE);
        internals.add(thumbView);
        handlerView.addView(childView, 0);
        hasSetup = true;
    }

    @Override
    public void setTrackBackground(Drawable drawable) {
        if (internals.size() == 0) return;
        for (IThumbHandlerProvider<View> thumb :
                internals) {
            SegmentConfig config = thumb.getSegmentConfig();
            config.maskBackground = drawable;
            thumb.applyConfig(config);
            thumb.reload();
        }
    }

    @Override
    public IThumbHandlerProvider<View> pushSegment(Segment segment, @Nullable SegmentConfig config) {
        IThumbHandlerProvider<View> thumbView = makeComponentProvider(config);
        View childView = makeComponent(segment, thumbView);
        childView.setTag(ChildTagKey, segment.getId());
        segmentList.add(segment);
        thumbs.add(thumbView);
        handlerView.addView(childView);
        handlerView.invalidate();
        return thumbView;
    }

    private IThumbHandlerProvider<View> makeComponentProvider(@Nullable SegmentConfig config) {
        Context context = mContextRef.get();
        IThumbHandlerProvider<View> thumbView = new ThumbSegmentProvider(context, config, mThumbActionListener);
        thumbView.setGroupAccessor(this);
        thumbView.setGroupId(groupId);
        thumbView.setParent(this);
        return thumbView;
    }


    private View makeComponent(Segment segment, IThumbHandlerProvider<View> thumbView) {
        ViewGroup childView = (ViewGroup) thumbView.provideView();
        if (segment == null) {
            segment = new Segment();
            segment.setFrom(0);
            segment.setTo(-1);
        }
        if (segment.getId() == 0) {
            segment.setId(System.currentTimeMillis());
        }
        thumbView.setSegment(segment);
        childView.setVisibility(View.INVISIBLE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        childView.setLayoutParams(params);
        return childView;
    }

    @Override
    public List<Segment> getAllSegment() {
        return segmentList;
    }

    @Override
    public IThumbHandlerProvider<View> pullSegment(int index) {
        IThumbHandlerProvider<View> thumb = thumbs.get(index);
        View view = thumb.provideView();
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
            segmentList.remove(index);
            thumbs.remove(index);
            return thumb;
        }
        return null;
    }

    @Override
    public IThumbHandlerProvider<View> pullSegmentById(long id) {
        int itemIndex = -1;
        for (int i = 0; i < segmentList.size(); i++) {
            if (segmentList.get(i).getId() == id) {
                itemIndex = i;
            }
        }
        if (itemIndex != -1) {
            return pullSegment(itemIndex);
        }
        return null;
    }

    @Override
    public void whenSelected(int index) {
        for (int i = 0; i < thumbs.size(); i++) {
            IThumbHandlerProvider<View> thumb = thumbs.get(i);
            if (index == i) {
                thumb.setSelected(!thumb.isSelected());
                if (mThumbActionListener != null) {
                    mThumbActionListener.onSelectionCheckedChange(thumb, thumb.getSegment(),
                            thumb.isSelected());
                }
            } else {
                thumb.setSelected(false);
            }
        }
    }

    @Override
    public void whenOffset(float centerAnchorX) {
        if (this.moving == null) this.moving = new UnionMovingTransition();
        this.moving.centerAnchorX = centerAnchorX;
        for (IThumbHandlerProvider<View> thumb :
                thumbs) {
            thumb.whenOffset(centerAnchorX);
        }
        for (IThumbHandlerProvider<View> thumb :
                internals) {
            thumb.whenOffset(centerAnchorX);
        }
    }

    @Override
    public void whenScroll(int totalScrollX) {
        if (this.moving == null) this.moving = new UnionMovingTransition();
        this.moving.totalScrollX = totalScrollX;
        for (IThumbHandlerProvider<View> thumb :
                thumbs) {
            thumb.whenScroll(totalScrollX);
        }
        for (IThumbHandlerProvider<View> thumb :
                internals) {
            thumb.whenScroll(totalScrollX);
        }
    }

    @Override
    public void whenContentLength(int contentLength) {
        if (this.moving == null) this.moving = new UnionMovingTransition();
        this.moving.contentLength = contentLength;
        for (IThumbHandlerProvider<View> thumb :
                thumbs) {
            thumb.whenContentLength(contentLength);
        }
        for (IThumbHandlerProvider<View> thumb :
                internals) {
            thumb.whenContentLength(contentLength);
        }
    }

    @Override
    public int getChildPosition(IThumbHandlerProvider<View> thumbSegmentProvider) {
        return thumbs.indexOf(thumbSegmentProvider);
    }

    @Override
    public IThumbHandlerProvider<View> previousNode(int index) {
        if (index == 0) return null;
        int thumbsSize = thumbs.size();
        if (index > 0 && index < thumbsSize) {
            return thumbs.get(index - 1);
        }
        return null;
    }

    @Override
    public IThumbHandlerProvider<View> nextNode(int index) {
        int thumbsSize = thumbs.size();
        if (index == thumbsSize - 1) return null;
        if (index >= 0 && index < thumbsSize) {
            return thumbs.get(index + 1);
        }
        return null;
    }

    @Override
    public View parentNode() {
        return handlerView;
    }

    @Override
    public void updateComponents() {
        for (IThumbHandlerProvider<View> thumb :
                thumbs) {
            thumb.reload();
        }
        for (IThumbHandlerProvider<View> thumb :
                internals) {
            thumb.reload();
        }
    }

    @Override
    public void uiShowProgressBorder(boolean isShowProgressBorder) {
        for (IThumbHandlerProvider<View> thumb :
                thumbs) {
            thumb.uiShowProgressBorder(isShowProgressBorder);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mTrackEventDispatcher != null) {
            if (mTrackEventDispatcher.ignore()) {
                return false;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v == handlerView) {
                int count = thumbs.size();
                Rect rect = new Rect();
                int[] location = new int[2];
                currentClickChildIndex = -1;
                for (int i = 0; i < count; i++) {
                    View segmentView = thumbs.get(i).provideView();
                    View child = segmentView.findViewById(R.id.thumb_clip_progress_layout);
                    if (child == null) {
                        continue;
                    }
                    int mW = child.getMeasuredWidth();
                    int mH = child.getMeasuredHeight();
                    location[0] = 0;
                    location[1] = 0;
                    child.getLocationOnScreen(location);
                    rect.left = location[0];
                    rect.top = location[1];
                    rect.bottom = mH + rect.top;
                    rect.right = mW + rect.left;
                    if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        currentClickChildIndex = i;
                        break;
                    }
                }
            }
        }
        return mGestureDetector.onTouchEvent(event);
    }
}
