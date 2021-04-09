package io.fuwafuwa.banjo;

import android.view.View;

import java.util.List;

import io.fuwafuwa.banjo.model.Segment;
import io.fuwafuwa.banjo.ui.ThumbNaiUnit;

public interface OnThumbActionListener {

    //仅适用定位线模式
    void onFocused(IThumbHandlerProvider<View> provider);

    void onUnFocused(IThumbHandlerProvider<View> provider);

    void onDragStart(IThumbHandlerProvider<View> provider, boolean isLeft);

    void onDragEnd(IThumbHandlerProvider<View> provider, boolean isLeft);

    void onDragging(IThumbHandlerProvider<View> provider, boolean isLeft);

    void onSegmentChange(IThumbHandlerProvider<View> thumbSegmentProvider, Segment segment);

    void onSelection(IThumbHandlerProvider<View> thumbSegmentProvider, Segment segment);

    void onSelectionCheckedChange(IThumbHandlerProvider<View> thumbSegmentProvider, Segment segment,boolean isSelected);

    void onDataSetChanged(List<ThumbNaiUnit> list);

    void onSegmentAdded(IThumbHandlerProvider<View> thumbView);

    void onSegmentRemoved(IThumbHandlerProvider<View> thumbView);
}
