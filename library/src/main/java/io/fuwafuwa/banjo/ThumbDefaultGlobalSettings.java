package io.fuwafuwa.banjo;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import io.fuwafuwa.banjo.extension.Frame;
import io.fuwafuwa.banjo.model.Size;
import io.fuwafuwa.banjo.profile.ExtractHandler;

public class ThumbDefaultGlobalSettings {

    private Config config;

    private ThumbDefaultGlobalSettings() {
        this.config = new Config();
    }

    public static class Config {

        public boolean userReactVibrate = true;
        /**
         * 选中区域边框
         */
        public boolean isShowProgressBorder = false;

        /**
         * 当内容未超出屏幕时，是否可以继续显示滚动模式
         */
        public boolean isScrollableWhenLess = true;

        /**
         * 是否支持时间轴滚动
         */
        public boolean isTimeLineMode = true;

        /**
         * 是否显示中线指示控件
         */
        public boolean isShowIndicator = true;

        /**
         * 中线指示控件颜色
         */
        @ColorInt
        public int indicatorColor = 0xFFFFFFFF;

        /**
         * range区域mask color
         */
        @ColorInt
        public int progressBarColor = Color.parseColor("#50FF4400");

        /**
         * 选择高亮区域颜色
         */
        @ColorInt
        public int progressBarSelectedColor = Color.parseColor("#99FF4400");

        /**
         * 是否允许滑块交叠
         */
        public boolean isThumbCanCross = false;

        /**
         * 中线超出长度
         */
        public int indicatorClipPaddingSize = 20;

        public LayoutMode layoutMode = LayoutMode.OVERLAY;

        /**
         * 缩略图尺寸
         */
        public Size thumbNailSize = new Size(96, Frame.dp2px(40));

        /**
         * 左右handler宽度
         */
        public float handlerWidth = Frame.dp2px(18f);

        public ExtractHandler preferThumbExtractor = ExtractHandler.FFMPEG;

        public int expandModeSpacing = Frame.dp2px(2f);
    }

    private static ThumbDefaultGlobalSettings instance;

    public static ThumbDefaultGlobalSettings builder() {
        if (instance == null) {
            synchronized (ThumbDefaultGlobalSettings.class) {
                instance = new ThumbDefaultGlobalSettings();
            }
        }
        return instance;
    }


    public Config build() {
        return config;
    }


    public static enum LayoutMode {
        OVERLAY(0),
        EXPAND_AFTER(1),
        EXPAND_BEFORE(2),
        EXPAND_AROUND(3),
        ;

        private int mode;

        private LayoutMode(int mode) {
        }

        public int getValue() {
            return mode;
        }

        public static LayoutMode fromInt(int ordinal) {
            for (LayoutMode mode : LayoutMode.values()) {
                if (ordinal == mode.mode) return mode;
            }
            return OVERLAY;
        }
    }

}
