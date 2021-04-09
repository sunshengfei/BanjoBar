package io.fuwafuwa.banjo;

import android.graphics.drawable.Drawable;

import io.fuwafuwa.banjo.model.Segment;
import io.fuwafuwa.banjo.profile.SegmentConfig;

public interface ThumbUniformUIRender {

    SegmentConfig getSegmentConfig();

    void setThumbSelectionBackground(Drawable drawable);

    void setThumbBackground(Drawable drawable);

    void setSegment(Segment segment);

    Segment getSegment();

    /**
     * 设置选中的区间
     * 基于长度为：contentLength
     *
     * @param start
     * @param to
     */
    void setRange(float start, float to);

    void applyConfig(SegmentConfig config);

    void setSelected(boolean isSelected);

    boolean isSelected();
}
