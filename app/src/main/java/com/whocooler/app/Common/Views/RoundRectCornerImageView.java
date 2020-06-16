package com.whocooler.app.Common.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundRectCornerImageView extends androidx.appcompat.widget.AppCompatImageView {

    private float radius = 18.0f;
    private Path path;
    private RectF rect;

    public RoundRectCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundRectCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundRectCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();
        setScaleType(ScaleType.CENTER_CROP);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(rect, rect.width(), rect.width(), Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
