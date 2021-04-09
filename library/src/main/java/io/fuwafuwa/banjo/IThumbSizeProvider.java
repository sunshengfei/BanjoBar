package io.fuwafuwa.banjo;

import android.view.View;

import io.fuwafuwa.banjo.model.Size;

public interface IThumbSizeProvider<T extends View, U> extends IHorizontalViewProvider<T, U> {
    void notifyDataChanged(int position);

    void setThumbSize(Size thumbNailSize);
}
