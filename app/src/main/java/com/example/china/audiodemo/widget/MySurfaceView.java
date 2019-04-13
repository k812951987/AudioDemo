package com.example.china.audiodemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.china.audiodemo.R;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;

    private Paint paint;

    private Bitmap bitmap;

    public MySurfaceView(Context context) {
        super(context);
        initView();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        holder = getHolder();

        holder.addCallback(this);

        paint = new Paint();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_img);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = this.holder.lockCanvas();
//
//        float scaleWight = ((float) getWidth()) / bitmap.getWidth();
//        float scaleHeight = ((float) getHeight()) / bitmap.getHeight();
//
//        float scaleSize = scaleWight>scaleHeight?scaleHeight:scaleWight;
//
//        Matrix matrix = new Matrix();
//        matrix.postScale(scaleSize, scaleSize);
//        Bitmap scaleBit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        Point center = new Point(getWidth() / 2, getHeight() / 2);
//        Point bmpCenter = new Point(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
//
//        Matrix centerMatrix = new Matrix();
////        centerMatrix.postScale(0.9f, 0.9f, center.x, center.y); // 中心点参数是有用的
//        centerMatrix.postTranslate(center.x - bmpCenter.x, center.y - bmpCenter.y);
//        canvas.drawBitmap(scaleBit, centerMatrix, paint);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        this.holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
