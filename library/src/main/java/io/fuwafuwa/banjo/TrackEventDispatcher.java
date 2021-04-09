package io.fuwafuwa.banjo;

import android.view.MotionEvent;

public interface TrackEventDispatcher {
    boolean onDispatchScroll(String groupId,MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

    boolean onDispatchFling(String groupId,MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);

    boolean ignore();
}
