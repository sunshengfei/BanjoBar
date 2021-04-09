package io.fuwafuwa.banjo;

import android.view.View;

import java.util.List;

public interface IHorizontalViewProvider<T extends View, U> extends IViewProvider<T> {
    void setDataSet(List<U> dataSets);

    int getContentLength();
}
