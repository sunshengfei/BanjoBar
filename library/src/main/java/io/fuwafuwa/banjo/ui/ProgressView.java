package io.fuwafuwa.banjo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import io.fuwafuwa.banjo.extension.Frame;

public class ProgressView extends View {

    int thickness = Frame.dp2px(3);
    int color = Color.WHITE;
    private int mHeight, mWidth;
    private Paint paint;

    public ProgressView(Context context) {
        super(context);
        initLayout(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(thickness);
        paint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //top
        float width = paint.getStrokeWidth();
        canvas.drawLine(0, width / 2, mWidth, width / 2, paint);
        //bottom
//        canvas.drawRect(rect, paint);
        canvas.drawLine(0, mHeight - width / 2, mWidth, mHeight - width / 2, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 100;
        if (MeasureSpec.UNSPECIFIED != wMode) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = 100;
        if (MeasureSpec.UNSPECIFIED != hMode) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
        this.mHeight = height;
        this.mWidth = width;
    }


    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
