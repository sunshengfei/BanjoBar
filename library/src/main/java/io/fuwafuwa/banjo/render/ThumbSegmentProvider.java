package io.fuwafuwa.banjo.render;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.ref.SoftReference;

import io.fuwafuwa.banjo.IThumbHandlerProvider;
import io.fuwafuwa.banjo.OnThumbActionListener;
import io.fuwafuwa.banjo.R;
import io.fuwafuwa.banjo.extension.T;
import io.fuwafuwa.banjo.ThumbDefaultGlobalSettings;
import io.fuwafuwa.banjo.ThumbGroupAccessNode;
import io.fuwafuwa.banjo.extension.SensorUtil;
import io.fuwafuwa.banjo.model.Segment;
import io.fuwafuwa.banjo.model.Size;
import io.fuwafuwa.banjo.profile.BarJustify;
import io.fuwafuwa.banjo.profile.SegmentConfig;
import io.fuwafuwa.banjo.profile.UnionMovingTransition;

public class ThumbSegmentProvider implements IThumbHandlerProvider<View>, View.OnTouchListener {

    private SegmentConfig config;
    private UnionMovingTransition moving;
    private SoftReference<Context> mContextRef;
    private View handlerView;
    private View thumbLeftHandler;
    private View thumbRightHandler;
    private View thumbProgress;
    private View thumbProgressSelect;
    private View thumbProgressMask;
    private AppCompatImageView thumbBackImage;
    private TextView thumbProgressLabel;
    private float minProgressRatio = 0.5f;
    private boolean walkStepMode = false;
    private int progressWidth;
    private int step = 140;

    private final float accuracySave = 2f;

    private float $_leftHandleBound = 0;
    private float $_rightHandleBound = 0;
    private ThumbGroupAccessNode<View> thumbGroupProvider;
    private float mDistanceOfLeftToStart;
    private float mDistanceOfRightToEnd;
    private GestureDetector mMaskGestureDetector;
    private OnThumbActionListener mThumbActionListener;

    private Segment segment;
    private float maxRangeWidth = 0;

    private String groupId;
    private ThumbGroupAccessNode<View> parentNode;

    private ThumbSegmentProvider(Context context) {
        this(context, null, null);
    }

    public ThumbSegmentProvider(Context context, SegmentConfig config, OnThumbActionListener thumbActionListener) {
        this.mContextRef = new SoftReference<>(context);
        if (config == null) {
            this.config = new SegmentConfig();
        } else {
            this.config = config;
        }
        if (this.moving == null) this.moving = new UnionMovingTransition();
        this.mThumbActionListener = thumbActionListener;
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public SegmentConfig getSegmentConfig() {
        return config;
    }

    @Override
    public void setThumbBackground(Drawable drawable) {
        if (drawable != null) {
            int mH = handlerView.getMeasuredHeight();
            if (mH == 0) {
                Size thumbNailSize = this.config.blockSize;
                if (thumbNailSize == null) {
                    thumbNailSize = ThumbDefaultGlobalSettings.builder().build().thumbNailSize;
                }
                mH = thumbNailSize.getHeight();
            }
            float heightRatio = mH * 1f / drawable.getIntrinsicHeight();
            if (heightRatio > 0) {
                thumbBackImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                thumbBackImage.setImageDrawable(drawable);
                thumbBackImage.setAdjustViewBounds(true);
            } else {
                thumbBackImage.setImageDrawable(drawable);
            }
        } else {
            //default
            thumbBackImage.setImageDrawable(null);
        }
    }

    /**
     * 本意可以使用thumbProgressBackground控件设置背景就可以，但是作为最上层遮罩
     *
     * @param drawable
     */
    @Override
    public void setThumbSelectionBackground(Drawable drawable) {
        if (drawable != null) {
            thumbProgressMask.setBackground(drawable);
        } else {
            //default
            thumbProgressMask.setBackground(null);
        }
    }

    @Override
    public void setSegment(Segment segment) {
        if (segment == null) {
            segment = new Segment();
        }
        this.segment = segment;
        thumbProgressMask.setTag(ThumbTrackProvider.ChildTagKey, segment.getId());
        thumbProgressLabel.setTag(ThumbTrackProvider.ChildTagKey, segment.getId());
        if (thumbProgressLabel == null) return;
        if (segment.getLabel() != null) {
            thumbProgressLabel.setText(segment.getLabel());
        }
    }

    @Override
    public Segment getSegment() {
        return segment;
    }


    @Override
    public View provideView() {
        if (handlerView != null) return handlerView;
        boolean insetMode = this.config.insetMode;
        int borderSize = this.config.borderSize;
        Context context = mContextRef.get();
        handlerView = View.inflate(context, R.layout.thumb_clip_handler, null);
        thumbLeftHandler = handlerView.findViewById(R.id.thumb_clip_left_handler);
        thumbRightHandler = handlerView.findViewById(R.id.thumb_clip_right_handler);
        thumbProgress = handlerView.findViewById(R.id.thumb_clip_progress);
        thumbProgressSelect = handlerView.findViewById(R.id.thumb_clip_selected);
        thumbProgressLabel = handlerView.findViewById(R.id.thumb_clip_tag);
        thumbProgressMask = handlerView.findViewById(R.id.thumb_clip_progress_layout);
        thumbBackImage = handlerView.findViewById(R.id.thumb_clip_background);
        thumbProgress.setMinimumWidth(100);
        thumbProgressMask.setMinimumWidth(100);
        handlerView.setTranslationY(insetMode ? 0 : borderSize);
        rebuiltHandler();
        thumbLeftHandler.setOnTouchListener(this);
        thumbRightHandler.setOnTouchListener(this);
        return handlerView;
    }

    private void rebuiltHandler() {
        Size thumbNailSize = this.config.blockSize;
        if (thumbNailSize == null) {
            thumbNailSize = new Size(120, 90);
        }
        if (thumbNailSize.getHeight() > 0) {
            boolean insetMode = this.config.insetMode;
            View parent = parentNode.parentNode();
            ViewGroup.LayoutParams params = thumbProgressMask.getLayoutParams();
            if (parent != null && parent.getLayoutParams() != null) {
                boolean parentWrapped = parent.getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
                boolean parentMatch = parent.getLayoutParams().height == ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                        || parent.getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT;
                if (insetMode || parentWrapped) {
                    int borderSize = this.config.borderSize;
                    params.height = thumbNailSize.getHeight() + (insetMode ? 0 : borderSize * 2);
                    thumbProgressMask.setLayoutParams(params);
                }
                if (parentMatch) {
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                thumbLeftHandler.getLayoutParams().height = params.height;
                thumbRightHandler.getLayoutParams().height = params.height;
                thumbProgressMask.setLayoutParams(params);
            }
        }
    }

    void drawProgress() {
        float pX = thumbLeftHandler.getX() + thumbLeftHandler.getMeasuredWidth();
        final float pWidth = Math.abs(thumbRightHandler.getX() - pX);
        progressWidth = (int) pWidth;
        thumbProgressMask.setX(pX);
        int realWith = progressWidth;//+ Math.abs(progressInset);
        ViewGroup.LayoutParams ll = thumbProgressMask.getLayoutParams();
        ll.width = realWith;
        thumbProgressMask.setLayoutParams(ll);
        thumbProgressMask.requestLayout();
        final float centerAnchorX = this.moving.centerAnchorX;
        final int contentLength = this.moving.contentLength;
        float progressStart = thumbLeftHandler.getX() + thumbLeftHandler.getWidth();
        float dL = progressStart - centerAnchorX;
        mDistanceOfLeftToStart = dL;
        mDistanceOfRightToEnd = contentLength - dL - pWidth;
        if (segment != null) {
            segment.setFrom(Math.round(mDistanceOfLeftToStart));
            segment.setTo(Math.round(dL + pWidth));
        }
        if (mThumbActionListener != null) {
            mThumbActionListener.onSegmentChange(this, segment);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (config.justify == BarJustify.READONLY || config.justify == BarJustify.FIXED) {
            setSelected(false);
            return false;
        }
        if (v == thumbLeftHandler) {
            if (config.justify == BarJustify.FIXED_LEFT) return false;
            return onLeftHandlerTouchEvent(v, event);
        } else if (v == thumbRightHandler) {
            if (config.justify == BarJustify.FIXED_RIGHT) return false;
            return onRightTouchEvent(v, event);
        }
        return false;
    }

    float rightDownX = 0f;

    private boolean onRightTouchEvent(View v, MotionEvent event) {
        final float centerAnchorX = this.moving.centerAnchorX;
        final int totalScrollX = this.moving.totalScrollX;
        final int contentLength = this.moving.contentLength;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            rightDownX = event.getX();
            if (mThumbActionListener != null) {
                mThumbActionListener.onDragStart(this, false);
            }
            v.setActivated(true);
            if (canVibrate()) {
                SensorUtil.vibrate(mContextRef.get());
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float xDistance = event.getX() - rightDownX;
            if (xDistance != 0f) {
                float newTransx = thumbRightHandler.getTranslationX() + xDistance;
                float progressStart = thumbLeftHandler.getX() + thumbLeftHandler.getWidth();
                float boundaryLeft = progressStart + minProgressRatio * contentLength;
                float newRightX = thumbRightHandler.getX() + xDistance;
                float progress = newRightX - progressStart;
                int leftDistance = contentLength - totalScrollX;
                if (maxRangeWidth > 0 && progress > maxRangeWidth) {
                    return true;
                }
                T.e("22get Cl", "====================");
                T.e("22get xDistance🤩", xDistance + "");
                T.e("22get Center🤩", centerAnchorX + "");
                T.e("22get LF🤩", thumbLeftHandler.getX() + "");
                T.e("22get RT🤩", thumbRightHandler.getX() + "/" + leftDistance);
                T.e("22get progress", progress + "|||");
                T.e("22get progressStart", progressStart + "|||");
                T.e("22get totalScrollX", totalScrollX + "||");
                T.e("22get Cl", contentLength + "|");
                T.e("22get maxRangeWidth", maxRangeWidth + "|");
                T.e("22get Cl", "====================");
                //right的 progress + left移动的X不能超过 contentLength
                //第一步:item主视图偏移
//                float mDx = centerAnchorX - totalScrollX;
                //第二步：计算leftHandler距离左边界长度dL
                float dL = progressStart - centerAnchorX;
//                float dL = progressStart - (contentLength - centerAnchorX);
                //dL +  progress <= contentLength
                float diffDx = dL + progress + $_rightHandleBound - contentLength;
                if (diffDx > 0) {
                    //精度补救
                    if (diffDx <= accuracySave) {
                        newTransx = newTransx - diffDx;
                    } else {
                        return true;
                    }
                }
                T.e("22get dL", "== " + dL);
                if (newRightX < boundaryLeft) {
                    return true;
                }
                ViewGroup.LayoutParams params = handlerView.getLayoutParams();
                params.width = (int) (newRightX + thumbRightHandler.getWidth());
                handlerView.setLayoutParams(params);
                if (walkStepMode) {
                    int stepCount = Math.round(newTransx / step);
                    thumbRightHandler.setTranslationX(stepCount * step);
                } else {
                    thumbRightHandler.setTranslationX(newTransx);
                }
                drawProgress();
                if (mThumbActionListener != null) {
                    mThumbActionListener.onDragging(this, false);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mThumbActionListener != null) {
                float progressStart = thumbLeftHandler.getX() + thumbLeftHandler.getWidth();
                float dL = progressStart - centerAnchorX;
                mThumbActionListener.onDragEnd(this, false);
                if (mThumbActionListener != null) {
                    mThumbActionListener.onSelection(this, segment);
                }
            }
            drawProgress();
            v.setActivated(false);
        }
        return true;
    }


    float leftDownX = 0f;

    private boolean onLeftHandlerTouchEvent(View v, MotionEvent event) {
        final float centerAnchorX = this.moving.centerAnchorX;
//        final int totalScrollX = this.moving.totalScrollX;
        final int contentLength = this.moving.contentLength;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            leftDownX = event.getX();
            if (mThumbActionListener != null) {
                mThumbActionListener.onDragStart(this, true);
            }
            v.setActivated(false);
            if (canVibrate()) {
                SensorUtil.vibrate(mContextRef.get());
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float xDistance = event.getX() - leftDownX;
            if (xDistance != 0f) {
                float newTransx = thumbLeftHandler.getTranslationX() + xDistance;
                float rightX = thumbRightHandler.getX();
                float boundaryRight = rightX - minProgressRatio * contentLength;
                float progressStart = thumbLeftHandler.getX() + thumbLeftHandler.getWidth();
                float ww = progressStart + xDistance;

                final float progress = Math.abs(rightX - ww);
                T.e("33get ", "****************************");
                T.e("33get ww = ", "[" + ww + "]");
                T.e("33get progressStart = ", "[" + progressStart + "]");
                T.e("33get rightx = ", "[" + thumbRightHandler.getX() + "]");
                T.e("33get progress = ", "[" + progress + "]");
                T.e("33get maxRangeWidth = ", "[" + maxRangeWidth + "]");
                T.e("33get boundaryRight = ", "[" + boundaryRight + "]");

                if (maxRangeWidth > 0 && progress > maxRangeWidth) {
                    return true;
                }
                float diffDx = centerAnchorX - $_leftHandleBound - ww;
//                if (ww < centerAnchorX - $_leftHandleBound) return true;
                T.e("33get diffDx = ", "[" + diffDx + "]");
                if (diffDx > 0) {
                    //精度补救
                    if (Math.abs(diffDx) <= accuracySave) {
                        newTransx = newTransx + diffDx;
                    } else {
                        return true;
                    }
                }
                if (ww > boundaryRight) {
                    return true;
                }
                if (walkStepMode) {
                    int stepCount = Math.round(newTransx / step);
                    thumbLeftHandler.setTranslationX(stepCount * step);
                } else {
                    thumbLeftHandler.setTranslationX(newTransx);
                }
                drawProgress();
                if (mThumbActionListener != null) {
                    mThumbActionListener.onDragging(this, true);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mThumbActionListener != null) {
//                float progressStart = thumbLeftHandler.getX() + thumbLeftHandler.getWidth();
//                float dL = progressStart - centerAnchorX;
                mThumbActionListener.onDragEnd(this, true);
                if (mThumbActionListener != null) {
                    mThumbActionListener.onSelection(this, segment);
                }
            }
            drawProgress();
            v.setActivated(false);
        }
        return true;
    }


    /**
     * 更新自身可活动坐标区域
     * 1、当Previous 元素变动【right bar ➡️,❎ 】时
     * 2、当After 元素变动【left bar ⬅️️,❎ 】时
     */
    @Override
    public void onUniformRegionChanged() {
        if (thumbGroupProvider == null) return;
        int position = thumbGroupProvider.getChildPosition(this);
        if (position == -1) return;
        IThumbHandlerProvider<View> previous = thumbGroupProvider.previousNode(position);
        if (previous == null) {
            $_leftHandleBound = 0;
        } else {
            $_leftHandleBound = previous.distanceOfLeftToStart();
        }
        IThumbHandlerProvider<View> next = thumbGroupProvider.nextNode(position);
        if (next == null) {
            $_rightHandleBound = 0;
        } else {
            $_rightHandleBound = next.distanceOfRightToEnd();
        }
    }

    @Override
    public void setSelected(boolean isSelected) {
        handlerView.setEnabled(true);
        switch (config.justify) {
            case FIXED:
                thumbProgressMask.setSelected(isSelected);
            case READONLY:
                if (BarJustify.READONLY == config.justify) {
                    handlerView.setEnabled(false);
                }
                thumbLeftHandler.setVisibility(View.INVISIBLE);
                thumbRightHandler.setVisibility(View.INVISIBLE);
                break;
            default:
                thumbProgressMask.setSelected(isSelected);
                thumbLeftHandler.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
                thumbRightHandler.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
                break;
        }
        if (isSelected) {
            handlerView.bringToFront();
        }
    }

    @Override
    public boolean isSelected() {
        return thumbProgressMask.isSelected();
    }

    @Override
    public void applyConfig(SegmentConfig config) {
        if (config != null) {
            this.config = config;
        }
        whenContentLength(this.config.contentLength);
        whenScroll(this.config.totalScrollX);
        whenOffset(this.config.centerAnchorX);
        setThumbSelectionBackground(new ColorDrawable(this.config.maskColor));
        if (this.config.maskBackground != null) {
            setThumbBackground(this.config.maskBackground);
        } else {
            setThumbBackground(new ColorDrawable(Color.TRANSPARENT));
        }
        Segment segment = getSegment();
        if (segment == null) {
            segment = new Segment();
            setSegment(segment);
        }
        uiShowProgressBorder(this.config.showBorder);
        if (this.segment == null) return;
        reloadRange();
        int[][] SELECTED_STATES =
                new int[][]{
                        new int[]{android.R.attr.state_selected}, // [0]
                        new int[]{-android.R.attr.state_selected}, // [1]
                };
        ColorStateList stateList = new ColorStateList(SELECTED_STATES,
                new int[]{
                        this.config.selectedColor,
                        Color.TRANSPARENT
                }
        );
        thumbProgressSelect.setBackgroundColor(Color.BLACK);
        thumbProgressSelect.setBackgroundTintList(stateList);
    }

    @Override
    public int distanceOfSegment() {
        return progressWidth;
    }

    @Override
    public int axisRange() {
        if (this.moving == null) return -1;
        return this.moving.contentLength;
    }

    @Override
    public float distanceOfLeftToStart() {
        return mDistanceOfLeftToStart;
    }

    @Override
    public float distanceOfRightToEnd() {
        return mDistanceOfRightToEnd;
    }

    @Override
    public void whenOffset(float centerAnchorX) {
        if (this.moving == null) this.moving = new UnionMovingTransition();
        this.moving.centerAnchorX = centerAnchorX;
//        thumbLeftHandler.setX(thumbLeftHandler.getX() + centerAnchorX - thumbLeftHandler.getMeasuredWidth());
//        thumbRightHandler.setX(thumbRightHandler.getX() + centerAnchorX + thumbLeftHandler.getMeasuredWidth());
    }

    @Override
    public void whenScroll(int totalScrollX) {
        if (this.moving == null) this.moving = new UnionMovingTransition();
        this.moving.totalScrollX = totalScrollX;
    }

    @Override
    public void whenContentLength(int contentLength) {
        if (this.moving == null) this.moving = new UnionMovingTransition();
        if (contentLength != 0) {
            this.moving.contentLength = contentLength;
        }
        if (thumbProgress != null) {
            thumbProgress.setMinimumWidth((int) (minProgressRatio * contentLength));
            thumbProgressMask.setMinimumWidth((int) (minProgressRatio * contentLength));
            if (config.maxRangeRatio > -1) {
                maxRangeWidth = config.maxRangeRatio * contentLength;
            }
        }
    }

    @Override
    public void setGroupAccessor(ThumbGroupAccessNode<View> thumbGroupProvider) {
        this.thumbGroupProvider = thumbGroupProvider;
        this.minProgressRatio = this.config.minRangeRatio;
    }


    private boolean canVibrate() {
        if (thumbGroupProvider == null || thumbGroupProvider.getGlobalSetting() == null)
            return false;
        ThumbDefaultGlobalSettings.Config setting = thumbGroupProvider.getGlobalSetting();
        return setting.userReactVibrate;
    }


    /**
     * 设置选中的区间，始终为进度条的区间 [start,start+len]
     * 基于长度为：contentLength
     *
     * @param start 起始位置
     * @param to    截止位置
     */
    @Override
    public void setRange(float start, float to) {
        float len = to - start;
        T.e("22get moving == null", (moving == null) + "");
        final int $handlerWidth = thumbLeftHandler.getMeasuredWidth();
        if ($handlerWidth == 0) {
            T.e("22get 」』」", "🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀");
            handlerView.post(() -> setRange(start, to));
            return;
        }
        if (this.moving == null) return;
        final int contentLength = this.moving.contentLength;
        final float adjStart = Math.max(start, 0f);
        float adjLen = len <= 0 ? contentLength - adjStart : Math.min(contentLength - adjStart, len);
        if (adjLen < minProgressRatio * contentLength) return;//不满足最小区间
        if (maxRangeWidth > 0 && adjLen > maxRangeWidth) {
            adjLen = (int) maxRangeWidth;
        }
        final float adjEnd = adjStart + adjLen;
        T.e("22get Range数据区间 ∈", "[" + adjStart + "," + adjEnd + "]");
        //place on layout (transition)
        final float centerAnchorX = this.moving.centerAnchorX;
        final int totalScrollX = this.moving.totalScrollX;
        float offsetLeftRev = centerAnchorX - $handlerWidth;// + (totalScrollX - contentLength);
        final float uiStart = offsetLeftRev + adjStart;
        final float uiEnd = offsetLeftRev + adjEnd + $handlerWidth;
        T.e("22get RangeUI区间 ∈", "[" + uiStart + "," + uiEnd + "]");
        T.e("22get totalScrollX", totalScrollX + "!");
        thumbLeftHandler.setTranslationX(uiStart);
        thumbRightHandler.setTranslationX(uiEnd);
        ViewGroup.LayoutParams params = handlerView.getLayoutParams();
//        params.width = (int) (Math.abs(uiEnd - (uiStart > 0 ? 0 : uiStart)) + thumbRightHandler.getWidth());
        params.width = (int) (offsetLeftRev + adjEnd + $handlerWidth * 2);
        T.e("22get width = ", params.width + "");
        handlerView.setLayoutParams(params);
        handlerView.setTranslationX(-totalScrollX);
        drawProgress();

    }

    public void reloadRange() {
        if (segment == null) return;
        float from = segment.getFrom();
        float to = segment.getTo();
        setRange(from, to);
        if (config.justify == BarJustify.READONLY || config.justify == BarJustify.FIXED || config.defaultHideHandler) {
            setSelected(false);
        }
    }

    @Override
    public void reload() {
        reloadRange();
        rebuiltHandler();
    }

    @Override
    public void setParent(ThumbGroupAccessNode<View> parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public void uiShowProgressBorder(boolean isShowProgressBorder) {
        thumbProgress.setVisibility(isShowProgressBorder ? View.VISIBLE : View.INVISIBLE);
    }
}
