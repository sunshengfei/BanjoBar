package io.fuwafuwa.banjo;

import android.view.View;

public interface IViewProvider<T extends View> {
    T provideView();
}
