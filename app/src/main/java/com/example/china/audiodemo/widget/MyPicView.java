package com.example.china.audiodemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.china.audiodemo.R;

public class MyPicView extends View {
    private Context context;
    private int src;

    public MyPicView(Context context) {
        super(context);
        initView(null);
    }

    public MyPicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public MyPicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        context = getContext();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyPicView);
        src = array.getResourceId(R.styleable.MyPicView_src, R.drawable.ic_launcher_background);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.ctid_black));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), src);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
    }
}
