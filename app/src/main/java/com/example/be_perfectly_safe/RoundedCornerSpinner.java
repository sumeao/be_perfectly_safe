package com.example.be_perfectly_safe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSpinner;

public class RoundedCornerSpinner extends AppCompatSpinner {
    public RoundedCornerSpinner(Context context) {
        super(context);
    }

    public RoundedCornerSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedCornerSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制四个1/4圆形
        int radius = 20; // 圆角半径，根据需要调整
        int padding = 10; // 边距，根据需要调整

        // 创建一个 Path，绘制四个1/4圆形
        Path path = new Path();
        RectF rect = new RectF(padding, padding, getWidth() - padding, getHeight() - padding);
        path.addRoundRect(rect, new float[]{radius, radius, 0, 0, 0, 0, radius, radius}, Path.Direction.CW);

        // 将 Path 应用到 Spinner
        canvas.clipPath(path);
    }
}
