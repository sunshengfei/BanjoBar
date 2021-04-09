package io.fuwafuwa.banjo;

public interface ThumbUniformServo {

    void onUniformRegionChanged();

    int distanceOfSegment();

    int axisRange();

    float distanceOfLeftToStart();

    float distanceOfRightToEnd();
}
