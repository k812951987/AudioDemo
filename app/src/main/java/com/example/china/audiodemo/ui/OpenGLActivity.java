package com.example.china.audiodemo.ui;

import android.content.Context;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.china.audiodemo.bean.TriAngleBean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLActivity extends BaseActivity {

    private MyGlSurfaceView myGlSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myGlSurfaceView = new MyGlSurfaceView(this);
        setContentView(myGlSurfaceView);
    }

    public class MyGlSurfaceView extends GLSurfaceView {

        private MyGLRenderer mRenderer;

        public MyGlSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(3);

            mRenderer = new MyGLRenderer();

            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(mRenderer);
        }

        public MyGlSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    public static class MyGLRenderer implements GLSurfaceView.Renderer {
        private TriAngleBean triangle;

        public static int loadShader(int type, String shaderCode) {
            //根据type创建顶点着色器或者片元着色器
            //创建一个vertex shader类型(GLES20.GL_VERTEX_SHADER)
            //或一个fragment shader类型(GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES31.glCreateShader(type);
            //将资源加入到着色器中，并编译
            GLES31.glShaderSource(shader, shaderCode);
            GLES31.glCompileShader(shader);
            return shader;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            triangle = new TriAngleBean();

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES31.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

            triangle.draw();
        }
    }
}
