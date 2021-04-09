package io.fuwafuwa.banjo.model;

import java.io.Serializable;

public class Segment implements Serializable {

    /**
     * 若为0 则自动随机，可能是时间戳、也可能是snowflake short序列
     */
    private long id;
    private int type; //类型
    private CharSequence label;
    private float from;
    private float to;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CharSequence getLabel() {
        return label;
    }

    public void setLabel(CharSequence label) {
        this.label = label;
    }

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }
}
