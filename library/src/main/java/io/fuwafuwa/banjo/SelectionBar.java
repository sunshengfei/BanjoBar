package io.fuwafuwa.banjo;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.fuwafuwa.banjo.extension.Frame;
import io.fuwafuwa.banjo.extension.T;
import io.fuwafuwa.banjo.model.Segment;
import io.fuwafuwa.banjo.model.Size;
import io.fuwafuwa.banjo.profile.SegmentConfig;
import io.fuwafuwa.banjo.profile.TrackConfig;
import io.fuwafuwa.banjo.render.ThumbScrollViewProvider;
import io.fuwafuwa.banjo.render.ThumbTrackProvider;
import io.fuwafuwa.banjo.ui.ThumbNaiUnit;

public class SelectionBar extends ConstraintLayout {
    protected Context mContext;
    private IThumbSizeProvider<RecyclerView, ThumbNaiUnit> thumbBoxProvider;

    protected final List<IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout>> thumbTrackList;

    //    @Deprecated
//    private IThumbGroupProvider<IThumbHandlerProvider<View>, FrameLayout> thumbGroupProvider;
    private int borderSize = Frame.dp2px(6);

    private float handlerWidth = Frame.dp2px(20);

    private boolean insetMode = true;

    private Size thumbNailSize;

    protected int rootWidth;
    private FrameLayout indicator;
    protected float centerAnchorX;
    //    protected int totalScrollX = 0;
    protected int mTotalScrollX = 0;
    protected int contentRange;
    private OnThumbActionListener mThumbActionListener;
    private OnTimeLineActionListener mTimeLineActionListener;
    private TrackEventDispatcher mTrackEventDispatcher;
    private OnSelectionBarStateEvent mSelectionBarStateEvent;
    private boolean isScrollableWhenLess;
    protected ThumbDefaultGlobalSettings.Config settings;
    private boolean recyclerViewUserInteract;

    private ViewTreeObserver.OnGlobalLayoutListener vic;
    private ConstraintLayout tracksContainer;
    protected List<ThumbNaiUnit> currentDataSet;

    public SelectionBar(@NonNull Context context) {
        super(context);
        thumbTrackList = new ArrayList<>();
        this.mContext = context;
        initLayout();
    }

    public SelectionBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectionBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        thumbTrackList = new ArrayList<>();
        this.mContext = context;
        settings = ThumbDefaultGlobalSettings.builder().build();
        parseAttr(attrs);
        initLayout();
    }

    private void parseAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.SelectionBar, 0, 0);
        try {
            settings.isShowIndicator = typedArray.getBoolean(R.styleable.SelectionBar_showIndicator, settings.isShowIndicator);
            settings.indicatorColor = typedArray.getColor(R.styleable.SelectionBar_indicatorColor, settings.indicatorColor);
            settings.layoutMode = ThumbDefaultGlobalSettings.LayoutMode.fromInt(typedArray.getInt(R.styleable.SelectionBar_displayMode,
                    ThumbDefaultGlobalSettings.LayoutMode.OVERLAY.getValue()));
            settings.isTimeLineMode = typedArray.getBoolean(R.styleable.SelectionBar_progressMode, settings.isTimeLineMode);
            settings.isScrollableWhenLess = typedArray.getBoolean(R.styleable.SelectionBar_contentAlwaysScroll, settings.isScrollableWhenLess);
            settings.isShowProgressBorder = typedArray.getBoolean(R.styleable.SelectionBar_showClipBorder, settings.isShowProgressBorder);
            settings.progressBarColor = typedArray.getColor(R.styleable.SelectionBar_progressBarColor, settings.progressBarColor);
            settings.userReactVibrate = typedArray.getBoolean(R.styleable.SelectionBar_vibrate, settings.userReactVibrate);
            int width = typedArray.getDimensionPixelSize(R.styleable.SelectionBar_thumbWidth, 0);
            int height = typedArray.getDimensionPixelSize(R.styleable.SelectionBar_thumbHeight, 0);
            float ratio = typedArray.getFloat(R.styleable.SelectionBar_thumbSizeRatio, 4 / 3f);
            if (ratio == 0 || (width == 0 && height == 0)) {
                //默认
                settings.thumbNailSize = new Size(96, (int) (96 / ratio));
            } else {
                if (width == 0) {
                    settings.thumbNailSize = new Size((int) (height * ratio), height);
                } else if (height == 0) {
                    settings.thumbNailSize = new Size(width, (int) (width / ratio));
                }
            }
        } finally {
            typedArray.recycle();
        }
    }

    public void setConfig(ThumbDefaultGlobalSettings.Config config) {
        if (config != null) {
            settings = config;
        }
        handlerWidth = settings.handlerWidth;
        thumbNailSize = settings.thumbNailSize;
    }

    public void updateConfig(ThumbDefaultGlobalSettings.Config config) {
        setConfig(config);
        setPadding(0, settings.indicatorClipPaddingSize, 0, settings.indicatorClipPaddingSize);
        RecyclerView recyclerView = thumbBoxProvider.provideView();
        ViewTreeObserver vt = recyclerView.getViewTreeObserver();
        if (vic != null) {
            vt.removeOnGlobalLayoutListener(vic);
            vt.addOnGlobalLayoutListener(vic);
        }
        /*thumbSlider*/
        if (tracksContainer != null && tracksContainer.getParent() != null) {
            reloadContainersLayout();
        }
        renderThumbGroupProviderList();
        /**/
        ImageView indicatorImage = (ImageView) indicator.getChildAt(0);
//        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        indicatorImage.setImageResource(R.drawable.indicator);
        FrameLayout.LayoutParams wrap = (FrameLayout.LayoutParams) indicatorImage.getLayoutParams();
        wrap.topMargin = -settings.indicatorClipPaddingSize;
        wrap.bottomMargin = -settings.indicatorClipPaddingSize;
        indicatorImage.setLayoutParams(wrap);
        defaultSettingsApply();
        updateLayout();
//        recyclerView.scrollToPosition(0);
    }

    private void reloadContainersLayout() {
        RecyclerView recyclerView = thumbBoxProvider.provideView();
        ConstraintLayout.LayoutParams tracksLayoutParams;
        if (tracksContainer.getParent() != null) {
            tracksLayoutParams = (LayoutParams) tracksContainer.getLayoutParams();
            tracksLayoutParams.width = LayoutParams.MATCH_CONSTRAINT;
            tracksLayoutParams.height = LayoutParams.WRAP_CONTENT;
        } else {
            tracksLayoutParams = new ConstraintLayout.LayoutParams(
                    LayoutParams.MATCH_CONSTRAINT,
                    LayoutParams.WRAP_CONTENT
            );
        }
        tracksLayoutParams.leftToLeft = recyclerView.getId();
        tracksLayoutParams.rightToRight = recyclerView.getId();
        tracksLayoutParams.topToBottom = LayoutParams.UNSET;
        tracksLayoutParams.bottomToBottom = LayoutParams.UNSET;
        tracksLayoutParams.topToTop = LayoutParams.UNSET;
        tracksLayoutParams.bottomToTop = LayoutParams.UNSET;
        tracksLayoutParams.topMargin = 0;
        tracksLayoutParams.bottomMargin = 0;
        if (settings.layoutMode == ThumbDefaultGlobalSettings.LayoutMode.EXPAND_AFTER) {
            tracksLayoutParams.topToBottom = recyclerView.getId();
            tracksLayoutParams.topMargin = Frame.dp2px(2);
        } else if (settings.layoutMode == ThumbDefaultGlobalSettings.LayoutMode.EXPAND_BEFORE) {
            tracksLayoutParams.bottomToTop = recyclerView.getId();
            tracksLayoutParams.bottomMargin = Frame.dp2px(2);
        } else {
            tracksLayoutParams.height = LayoutParams.MATCH_CONSTRAINT;
            tracksLayoutParams.topToTop = recyclerView.getId();
            tracksLayoutParams.bottomToBottom = recyclerView.getId();
        }
        if (tracksContainer.getParent() == null) {
            addView(tracksContainer, tracksLayoutParams);
        } else {
            tracksContainer.setLayoutParams(tracksLayoutParams);
        }
    }

    protected void initLayout() {
        thumbTrackList.clear();
        removeAllViews();
        setConfig(null);
        //- Space
//        Space topSpace = new Space(mContext);
//        topSpace.setId(generateViewId());
//        ConstraintLayout.LayoutParams topLayparams = new ConstraintLayout.LayoutParams(
//                0,
//                0
//        );
//        topLayparams.topToTop = LayoutParams.PARENT_ID;
//        topLayparams.topMargin = -120;
//        addView(topSpace, topLayparams);
        //+0 HorizontalRecyclerView
        setClipToPadding(false);
        setClipChildren(false);
        if (thumbBoxProvider == null) {
            thumbBoxProvider = new ThumbScrollViewProvider(mContext);
        }
        RecyclerView recyclerView = thumbBoxProvider.provideView();
        thumbBoxProvider.setThumbSize(thumbNailSize);
        recyclerView.scrollToPosition(0);
        recyclerViewUserInteract = false;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int cr = recyclerView.computeHorizontalScrollRange();
                int tsx = recyclerView.computeHorizontalScrollOffset();
//                T.e("22get 卧槽tsx 😪", tsx + "!" + dx);
                mTotalScrollX += dx;
                int totalScrollX = 0;
                if (tsx != 0) {
                    totalScrollX = tsx;
                    mTotalScrollX = totalScrollX;
                } else if (dx < 0) {
                    totalScrollX = 0;
                    mTotalScrollX = 0;
                }
                T.e("🦊🦊", "onScrolled");
//                T.e("🦊", "onScrolled,cr=" + cr + "，totalScrollX1=" + totalScrollX1);
                int oldCR = contentRange;
                if (cr != 0) {
                    contentRange = cr;
                } else {
                    evaluateContentLength(recyclerView);
                }
                if (oldCR != contentRange) {
                    reloadCenterAnchorX();
                    if (mSelectionBarStateEvent != null)
                        mSelectionBarStateEvent.xAxisBarSizeChanged(contentRange);
                }
                boxScrolled(dx, contentRange, true);
//                T.e("22get 卧槽", totalScrollX + "!");
                T.e("22get 卧槽卧槽", contentRange + "//" + totalScrollX + "!" + mTotalScrollX);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    recyclerViewUserInteract = true;
                    if (mTimeLineActionListener != null) {
                        mTimeLineActionListener.onDragStart();
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerViewUserInteract = false;
                    if (mTimeLineActionListener != null) {
                        mTimeLineActionListener.onDragEnd();
                    }
                } else {
                    recyclerViewUserInteract = false;
                }
            }
        });
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        layoutParams.topToTop = LayoutParams.PARENT_ID;
        layoutParams.leftToLeft = LayoutParams.PARENT_ID;
        layoutParams.rightToRight = LayoutParams.PARENT_ID;
//        layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        if (insetMode) {
            layoutParams.bottomMargin = 0;
        } else {
            layoutParams.bottomMargin = borderSize;
        }
        ViewTreeObserver vt = recyclerView.getViewTreeObserver();
        vic = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                T.e("🦊", "onGlobalLayout");
                int oldCR = contentRange;
                int cr = recyclerView.computeHorizontalScrollRange();
                if (cr == 0) {
                    evaluateContentLength(recyclerView);
                }
                if (cr != 0) {
                    int tsx = recyclerView.computeHorizontalScrollOffset();
                    mTotalScrollX = tsx;
                    T.e("🦁 tsx ", "tsx = " + tsx);
                    contentRange = cr;
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    updateComponents();
                    if (oldCR != contentRange) {
                        reloadCenterAnchorX();
                        if (mSelectionBarStateEvent != null) {
                            mSelectionBarStateEvent.xAxisBarSizeChanged(contentRange);
                        }
                        for (IThumbTrackProvider thumbGroupProvider : thumbTrackList) {
                            thumbGroupProvider.whenContentLength(contentRange);
                        }
                    }
                }
            }
        };
        vt.addOnGlobalLayoutListener(vic);
        recyclerView.setClipToPadding(false);
        if (recyclerView.getParent() != null) {
            recyclerView.setLayoutParams(layoutParams);
        } else {
            addView(recyclerView, layoutParams);
        }
        //+1 handler [+] more
//        ThumbSegmentProvider.Config config = new ThumbSegmentProvider.Config();
//        config.insetMode = insetMode;
//        config.borderSize = borderSize;
//        config.handlerWidth = handlerWidth;
//        config.thumbNailSize = thumbNailSize;
//        config.maskColor = settings.progressBarColor;
        mTrackEventDispatcher = new TrackEventDispatcher() {
            @Override
            public boolean onDispatchScroll(String groupId, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (ignore()) {
                    return false;
                }
                T.e("🖱", "onDispatchScroll,distanceX=" + distanceX);
                recyclerView.scrollBy((int) distanceX, 0);
                return true;
            }

            @Override
            public boolean onDispatchFling(String groupId, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (ignore()) {
                    return false;
                }
                recyclerView.fling((int) -velocityX, 0);
                return true;
            }

            @Override
            public boolean ignore() {
                return settings.layoutMode == ThumbDefaultGlobalSettings.LayoutMode.OVERLAY;
            }
        };
        if (tracksContainer == null) {
            tracksContainer = new ConstraintLayout(mContext);
        }
        reloadContainersLayout();
        addThumbGroup();
        //+2 indicator
        ConstraintLayout.LayoutParams indicatorParams = new ConstraintLayout.LayoutParams(
                Frame.dp2px(1.5f),
                LayoutParams.MATCH_CONSTRAINT
        );

        if (indicator == null) {
            ImageView indicatorImage = new AppCompatImageView(mContext);
//        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
            indicatorImage.setImageResource(R.drawable.indicator);
            FrameLayout.LayoutParams wrap = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            wrap.topMargin = -settings.indicatorClipPaddingSize;
            wrap.bottomMargin = -settings.indicatorClipPaddingSize;
            indicator = new FrameLayout(mContext);
            indicator.addView(indicatorImage, wrap);
        }

        indicatorParams.leftToLeft = LayoutParams.PARENT_ID;
        indicatorParams.rightToRight = LayoutParams.PARENT_ID;
        indicatorParams.bottomToBottom = LayoutParams.PARENT_ID;
        indicatorParams.topToTop = LayoutParams.PARENT_ID;
        if (indicator.getParent() != null) {
            indicator.setLayoutParams(indicatorParams);
        } else {
            addView(indicator, indicatorParams);
        }
        defaultSettingsApply();
    }

    protected void renderThumbGroupProviderList() {
        for (int i = 0; i < thumbTrackList.size(); i++) {
            IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout> thumbGroupProviderItem = thumbTrackList.get(i);
            if (thumbGroupProviderItem != null) {
                appendOrUpdateThumbGroupItem(thumbGroupProviderItem, i);
            }
        }
    }


    protected void appendOrUpdateThumbGroupItem(@NonNull IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout> iThumbTrackProvider,
                                                int position) {
        if (thumbBoxProvider == null) {
            return;
        }
        if (tracksContainer == null) {
            return;
        }
        ViewGroup trackView = iThumbTrackProvider.provideView();
        boolean newItem = trackView.getParent() == null;
        int previousId;
        if (position == 0 || tracksContainer.getChildCount() == 0) {
            previousId = LayoutParams.UNSET;
        } else {
            View lastItemView;
            if (position < 0 || newItem) {
                lastItemView = tracksContainer.getChildAt(tracksContainer.getChildCount() - 1);
            } else {
                lastItemView = tracksContainer.getChildAt(position - 1);
            }
            previousId = lastItemView.getId();
        }

        ViewGroup.LayoutParams handler2 = trackView.getLayoutParams();
        if (handler2 == null) {
            handler2 = new ConstraintLayout.LayoutParams(
                    LayoutParams.MATCH_CONSTRAINT,
                    LayoutParams.WRAP_CONTENT
            );
        }
        ConstraintLayout.LayoutParams handler = (LayoutParams) handler2;
        handler.leftToLeft = LayoutParams.PARENT_ID;
        handler.rightToRight = LayoutParams.PARENT_ID;
        handler.topToBottom = LayoutParams.UNSET;
        handler.topToTop = LayoutParams.UNSET;
        handler.bottomToBottom = LayoutParams.UNSET;
        handler.bottomToTop = LayoutParams.UNSET;
        handler.bottomMargin = 0;
        handler.topMargin = 0;
        if (settings.layoutMode == ThumbDefaultGlobalSettings.LayoutMode.EXPAND_AFTER) {
            if (previousId == LayoutParams.UNSET) {
                handler.topToTop = LayoutParams.PARENT_ID;
            } else {
                handler.topToBottom = previousId;
                handler.topMargin = settings.expandModeSpacing;
            }
            handler2.height = LayoutParams.WRAP_CONTENT;
        } else if (settings.layoutMode == ThumbDefaultGlobalSettings.LayoutMode.EXPAND_BEFORE) {
            if (previousId == LayoutParams.UNSET) {
                handler.bottomToBottom = LayoutParams.PARENT_ID;
            } else {
                handler.bottomToTop = previousId;
                handler.bottomMargin = settings.expandModeSpacing;
            }
            handler2.height = LayoutParams.WRAP_CONTENT;
        } else {
            handler.topToTop = LayoutParams.PARENT_ID;
            handler.bottomToBottom = LayoutParams.PARENT_ID;
            handler2.height = LayoutParams.MATCH_CONSTRAINT;
        }
        if (trackView.getParent() == null) {
            tracksContainer.addView(trackView, handler);
        } else {
            trackView.setLayoutParams(handler);
        }
        iThumbTrackProvider.updateComponents();
    }

    public IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout> addThumbGroup() {
        return addThumbGroup(null);
    }

    public IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout> addThumbGroup(TrackConfig trackConfig) {
        ThumbTrackProvider itemProvider = new ThumbTrackProvider(mContext, trackConfig, mThumbActionListener, mTrackEventDispatcher);
        itemProvider.whenScroll(mTotalScrollX);
        itemProvider.whenContentLength(contentRange);
        itemProvider.whenOffset(centerAnchorX);
        appendOrUpdateThumbGroupItem(itemProvider, -1);
        thumbTrackList.add(itemProvider);
//        itemProvider.applyTrackConfig(trackConfig);
        return itemProvider;
    }

    private void evaluateContentLength(@NonNull RecyclerView recyclerView) {
        if (recyclerView.getAdapter() != null) {
            int count = recyclerView.getAdapter().getItemCount();
            contentRange = thumbNailSize.getWidth() * count;
        }
    }

    private void defaultSettingsApply() {
        if (indicator.getChildCount() > 0) {
            AppCompatImageView imageView = (AppCompatImageView) indicator.getChildAt(0);
            imageView.setImageTintList(ColorStateList.valueOf(settings.indicatorColor));
        }
        indicator.setVisibility(settings.isShowIndicator ? VISIBLE : GONE);
        isScrollableWhenLess = settings.isScrollableWhenLess;
        for (IThumbTrackProvider thumbGroupProvider : thumbTrackList) {
            thumbGroupProvider.uiShowProgressBorder(settings.isShowProgressBorder);
            if (thumbGroupProvider instanceof ThumbGroupAccessNode) {
                ((ThumbGroupAccessNode) thumbGroupProvider).notifyGlobalSetting(settings);
            }
        }

//        thumbGroupProvider.uiShowProgressBorder(settings.isShowProgressBorder);
//        if (thumbGroupProvider instanceof ThumbGroupAccessNode) {
//            ((ThumbGroupAccessNode) thumbGroupProvider).notifyGlobalSetting(settings);
//        }
    }

    private void reloadCenterAnchorX() {
        if (contentRange != 0) {
            boolean less = (contentRange + handlerWidth * 2 < rootWidth);
            boolean isScrollable = isScrollableWhenLess || !less;
            if (isScrollable) {
                indicator.setVisibility(VISIBLE);
            } else {
                indicator.setVisibility(GONE);
            }
            int padding = isScrollableWhenLess || !less ? rootWidth : (rootWidth - contentRange);
            centerAnchorX = padding / 2f;
            T.e("🦊", "rootWidth=" + rootWidth + "，contentRange=" + contentRange + ",thumbNailSize" + thumbNailSize.getWidth());
        } else {
            centerAnchorX = rootWidth / 2f;
        }
    }

    private void updateLayout() {
        reloadCenterAnchorX();
        thumbBoxProvider.setThumbSize(thumbNailSize);
        RecyclerView recyclerView = thumbBoxProvider.provideView();
        recyclerView.setPadding((int) centerAnchorX, 0, (int) centerAnchorX, 0);
        for (IThumbTrackProvider thumbGroupProvider : thumbTrackList) {
            thumbGroupProvider.whenOffset(centerAnchorX);
        }
    }

    private void boxScrolled(int dx, int contentLength, boolean contentChange) {
        T.e("\uD83D\uDDB1===\uD83D\uDDB1🦊", "boxScrolled，dx=" + dx);
        for (IThumbTrackProvider thumbGroupProvider : thumbTrackList) {
            ViewGroup thumbSlider = (ViewGroup) thumbGroupProvider.provideView();
            int count = thumbSlider.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = thumbSlider.getChildAt(i);
//                if (child instanceof ImageView) continue;
                child.setTranslationX(child.getTranslationX() - dx);
            }
            thumbGroupProvider.whenScroll(mTotalScrollX);
            if (contentLength != 0 && contentChange) {
                thumbGroupProvider.whenContentLength(contentLength);
            }
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int temW = getMeasuredWidth();
        if (rootWidth != temW) {
            rootWidth = temW;
            updateLayout();
        }
    }


    //region  开放API

    public void dataSet(List<ThumbNaiUnit> list) {
        T.e("🦊", "准备setDataSet");
//        mTotalScrollX = 0;
        currentDataSet = list;
        if (mThumbActionListener != null) {
            mThumbActionListener.onDataSetChanged(list);
        }
        thumbBoxProvider.setDataSet(list);
        //todo 根据缓存位置数据更新thumbsView位置
        post(this::reLayout);
    }

    public void reLayout() {
        RecyclerView recyclerView = thumbBoxProvider.provideView();
        if (contentRange == 0) {
            evaluateContentLength(recyclerView);
        }
        boxScrolled(0, contentRange, true);
        updateLayout();
//        T.e("🦊", "updateComponents");
    }

    public void updateComponents() {
        updateScrollPosition();
        for (IThumbTrackProvider thumbGroupProvider : thumbTrackList) {
            thumbGroupProvider.updateComponents();
        }
    }

    public void updateScrollPosition() {
        for (IThumbTrackProvider thumbGroupProvider : thumbTrackList) {
            thumbGroupProvider.whenScroll(mTotalScrollX);
        }
    }

    public void notifyDataChanged(int position) {
        thumbBoxProvider.notifyDataChanged(position);
    }


    public void addThumb(float from, float to) {
        addThumb(from, to, 0);
    }

    public void addThumb(float from, float to, int trackIndex) {
        Segment segment = new Segment();
        segment.setFrom(from);
        segment.setTo(to);
        segment.setLabel("");
        addThumb(segment, trackIndex);
    }

    public void addThumb(Segment segment, int trackIndex) {
        addThumb(segment, trackIndex, null);
    }

    public void addThumb(Segment segment, int trackIndex, SegmentConfig config) {
        if (config == null) {
            config = new SegmentConfig();
            config.insetMode = insetMode;
            config.borderSize = borderSize;
            config.blockSize = thumbNailSize;
            config.maskColor = settings.progressBarColor;
            config.showBorder = settings.isShowProgressBorder;
            config.selectedColor = settings.progressBarSelectedColor;
        }
        config.centerAnchorX = centerAnchorX;
        config.totalScrollX = mTotalScrollX;
        config.contentLength = contentRange;
        SegmentConfig finalConfig = config;
        post(() -> {
            reLayout();
            if (trackIndex > -1 && trackIndex < thumbTrackList.size()) {
                IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout> thumbGroupProvider = thumbTrackList.get(trackIndex);
                thumbGroupProvider.pushSegment(segment, finalConfig);
//                thumbGroupProvider.uiShowProgressBorder(settings.isShowProgressBorder);
            }
        });
    }

    public void trashThumb(int index, int trackIndex) {
        if (trackIndex > -1 && trackIndex < thumbTrackList.size()) {
            IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout> thumbGroupProvider = thumbTrackList.get(trackIndex);
            thumbGroupProvider.pullSegment(index);
        }
    }

    public void setThumbActionListener(OnThumbActionListener mThumbActionListener) {
        this.mThumbActionListener = mThumbActionListener;
        for (IThumbTrackProvider thumbGroupProvider : thumbTrackList) {
            thumbGroupProvider.setThumbActionListener(mThumbActionListener);
        }
    }

    public void setTimeLineActionListener(OnTimeLineActionListener mTimeLineActionListener) {
        this.mTimeLineActionListener = mTimeLineActionListener;
    }

    public void setSelectionBarStateEvent(OnSelectionBarStateEvent mSelectionBarStateEvent) {
        this.mSelectionBarStateEvent = mSelectionBarStateEvent;
    }

    private Interpolator interpolator = new LinearInterpolator();

    /**
     * 在seconds内走完contentRange长度
     *
     * @param seconds
     * @param spanMills
     */
    public void scrollStart(int seconds, Long spanMills) {
        T.e("🐶🐶🐶", "recyclerViewUserInteract=" + recyclerViewUserInteract);
        if (contentRange > 0 && settings.isTimeLineMode) {
            RecyclerView recyclerView = thumbBoxProvider.provideView();
//            if (recyclerView.isHovered()) return;
            if (recyclerViewUserInteract) return;
//            if (!recyclerView.canScrollHorizontally(1)) return;
            int distancePerSecond = contentRange / seconds;//每一秒移动距离
            int count = (int) (1000 / spanMills); //每一秒包含多少个spanMills
            float progressStep = distancePerSecond * 1f / count;
            T.e("🐶", "※※※※※※※※※※※※※※※");
            T.e("🐶", "seconds=" + seconds);
            T.e("🐶", "contentRange=" + contentRange);
            T.e("🐶", "distancePerSecond=" + distancePerSecond);
            T.e("🐶", "progressStep=" + progressStep);
            recyclerView.smoothScrollBy(Math.round(progressStep), 0, interpolator, 0);
//            recyclerView.setScrollX((int) progress);
        }
    }


    @Nullable
    public List<IThumbTrackProvider<IThumbHandlerProvider<View>, FrameLayout>> getTrackList() {
        return thumbTrackList;
    }
    //endregion

}
