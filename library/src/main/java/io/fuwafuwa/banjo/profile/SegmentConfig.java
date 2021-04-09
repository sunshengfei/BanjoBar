package io.fuwafuwa.banjo.profile;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import io.fuwafuwa.banjo.model.Size;

public class SegmentConfig extends UnionMovingTransition {

    //region -- justify
    public BarJustify justify = BarJustify.DEFAULT;
    public boolean stepMode;
    //endregion

    //region -- input_presentation
    public Size blockSize;//区块大小
    //endregion

    //region -- presentation
    public int maskColor = 0x50ff4400;
    public boolean insetMode;
    public int borderSize;
    public float maxRangeRatio = 1f; //1x，最大与主轴同长度,暂未加入超出主轴长度
    /**
     * 最小宽度比例：1 percent
     * 最终pixel宽度 = 【UI progress长度】 * 0.01
     */
    public float minRangeRatio = 0.01f;
    public boolean showBorder;
    public boolean defaultHideHandler;
    public int selectedColor = maskColor;
    @Nullable
    public Drawable maskBackground;
    //endregion
}
