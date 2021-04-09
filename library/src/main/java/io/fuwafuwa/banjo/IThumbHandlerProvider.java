package io.fuwafuwa.banjo;

import android.view.View;

public interface IThumbHandlerProvider<T extends View> extends IViewProvider<T>, ThumbUniformServo, ThumbUniformUIRender {

    void whenOffset(float centerAnchorX);

    void whenScroll(int totalScrollX);

    void whenContentLength(int contentLength);

    void setGroupAccessor(ThumbGroupAccessNode<T> thumbGroupProvider);

    void uiShowProgressBorder(boolean isShowProgressBorder);

    void setGroupId(String groupId);

    String getGroupId();

    void reload();

    void setParent(ThumbGroupAccessNode<T> parentNode);
}
