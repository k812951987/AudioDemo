package com.example.china.audiodemo.ui;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.china.audiodemo.R;
import com.example.china.audiodemo.widget.NoMissButton;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener, Camera.PreviewCallback {
    @BindView(R.id.surface_bt)
    NoMissButton surfaceBt;
    @BindView(R.id.texture_bt)
    NoMissButton textureBt;
    @BindView(R.id.camera_layout)
    LinearLayout cameraLayout;

    private boolean isCamera;
    private Camera camera;
    private SurfaceView mSurface;
    private TextureView mTexture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.surface_bt, R.id.texture_bt})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.surface_bt:
                if (isCamera) {
                    surfaceBt.setText("Surface");
                    stopSurfaceCamera();
                } else {
                    surfaceBt.setText("停止预览");
                    startSurfaceCamera();
                }
                break;
            case R.id.texture_bt:
                if (isCamera) {
                    textureBt.setText("Texture");
                    stopTextureCamera();
                } else {
                    startTextureCamera();
                    textureBt.setText("停止预览");
                }
                break;
        }
    }

    private void startTextureCamera() {
        isCamera = true;
        mTexture = new TextureView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        mTexture.setLayoutParams(params);
        cameraLayout.addView(mTexture);

        mTexture.setSurfaceTextureListener(this);
        camera = Camera.open();
        camera.setDisplayOrientation(90);

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        camera.setParameters(parameters);
        camera.setPreviewCallback(this);
    }

    private void stopTextureCamera() {
        cameraLayout.removeView(mTexture);
    }

    private void stopSurfaceCamera() {
        isCamera = false;
        camera.stopPreview();
        camera.release();
        camera = null;
        mSurface.removeCallbacks(null);
        cameraLayout.removeView(mSurface);
    }

    private void startSurfaceCamera() {
        isCamera = true;
        mSurface = new SurfaceView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        mSurface.setLayoutParams(params);
        cameraLayout.addView(mSurface);
        mSurface.getHolder().addCallback(this);

        camera = Camera.open();
        camera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            camera.setPreviewTexture(surface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (!isCamera)
            return false;
        mTexture.removeCallbacks(null);
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.lock();
        camera.release();
        camera = null;
        isCamera = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private boolean one;

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        Logger.d(data.length);
    }
}
