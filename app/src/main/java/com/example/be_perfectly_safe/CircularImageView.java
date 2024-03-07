package com.example.be_perfectly_safe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CircularImageView extends AppCompatImageView {
    private Paint paint;
    private Bitmap bitmap;
    private BitmapShader shader;

    // 構造函數，初始化圖片覆蓋效果
    public CircularImageView(Context context) {
        super(context);
        init();
    }

    // 構造函數，初始化圖片覆蓋效果
    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 構造函數，初始化圖片覆蓋效果
    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 初始化函數，設置畫筆抗鋸齒屬性
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    // 覆寫onDraw方法，實現圖片圓形裁剪效果
    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            // 創建圖片覆蓋效果
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);

            // 設置圓形半徑為寬高中較小值的一半
            int radius = Math.min(getWidth(), getHeight()) / 2;
            // 繪製圓形圖片
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
        }

        super.onDraw(canvas);
    }

    // 覆寫setImageBitmap方法，設置圖片並觸發重繪
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.bitmap = bm;
    }
}
