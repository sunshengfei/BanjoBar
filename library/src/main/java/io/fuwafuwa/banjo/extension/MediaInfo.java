package io.fuwafuwa.banjo.extension;


import io.fuwafuwa.banjo.model.Size;

public class MediaInfo {
    private Size size;
    private long duration;
    private int rotation;

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
