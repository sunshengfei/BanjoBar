package io.fuwafuwa.banjo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.View;

import io.fuwafuwa.banjo.profile.TrackConfig;

public interface ThumbGroupAccessNode<T extends View> {

    IThumbHandlerProvider<T> previousNode(int index);

    IThumbHandlerProvider<T> nextNode(int index);

    T parentNode();

    void applyTrackConfig(TrackConfig trackConfig);

    TrackEventDispatcher getTrackEventDispatcher();

    void notifyGlobalSetting(ThumbDefaultGlobalSettings.Config config);

    ThumbDefaultGlobalSettings.Config getGlobalSetting();

    void whenSelected(int index);

    int getChildPosition(IThumbHandlerProvider<T> thumbSegmentProvider);

    void setTrackBackground(Drawable drawable);
}
