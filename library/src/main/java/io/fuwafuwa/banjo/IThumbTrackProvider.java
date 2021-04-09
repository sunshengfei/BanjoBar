package io.fuwafuwa.banjo;

import android.view.View;

import java.util.Collection;
import java.util.List;

import io.fuwafuwa.banjo.model.Segment;
import io.fuwafuwa.banjo.profile.SegmentConfig;

/**
 * @param <P> IThumbHandlerProvider<? extend View>,容器代理可控的组件
 * @param <T> ? extends View，最终生成的容器
 * @author fred 2021-03-30 22:45:38
 */
public interface IThumbTrackProvider<P, T extends View> extends IViewProvider<T> {

    String groupId();

    Collection<P> provideViews();

    P pushSegment(Segment segment, SegmentConfig config);

    P pullSegment(int index);

    P pullSegmentById(long id);

    List<Segment> getAllSegment();

    void whenOffset(float centerAnchorX);

    void whenScroll(int totalScrollX);

    void whenContentLength(int contentLength);

    void setThumbActionListener(OnThumbActionListener mThumbActionListener);

    void updateComponents();

    void uiShowProgressBorder(boolean isShowProgressBorder);
}
