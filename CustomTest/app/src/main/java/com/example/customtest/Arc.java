package com.example.customtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * create by WenJinG on 2019/4/25
 */
public class Arc extends View {

    private int w;
    private int h;
    private Paint paint;
    private boolean isRotate = false;
    public Arc(Context context) {
        super(context);
    }

    public Arc(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
    }

    public Arc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Arc(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        @SuppressLint("DrawAllocation") RectF rect = new RectF((w-h/3)/2,0,(w-h/3)/2+h/3,h/3);
        @SuppressLint("DrawAllocation") RectF rect2 = new RectF((w-h/3)/2,h*2/3,(w-h/3)/2+h/3,h);
        canvas.drawArc(rect,0,-180,true,paint);
        canvas.drawArc(rect2,0,180,true,paint);
        if(isRotate)
        canvas.rotate(180);
    }

    public void setRotate(boolean rotate) {
        isRotate = rotate;
    }
}
