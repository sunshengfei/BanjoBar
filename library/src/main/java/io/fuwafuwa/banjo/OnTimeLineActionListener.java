package io.fuwafuwa.banjo;

public interface OnTimeLineActionListener {

    /**
     * 时间轴开始拖动
     */
    void onDragStart();


    /**
     * 时间轴结束拖动
     */
    void onDragEnd();
}
