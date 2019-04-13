package com.example.china.audiodemo.bean;

import android.opengl.GLES31;

import com.example.china.audiodemo.ui.OpenGLActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TriAngleBean {

    public final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    public final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final int mProgram;

    private FloatBuffer vertexBuffer;

    private int mPositionHandle;
    private int mColorHandle;
    // // 数组中每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;

    static float triangleCoords[] = {   // 按逆时针方向顺序
            0.0f, 0.622008459f, 1.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

    // 设置颜色，分别为red, green, blue 和alpha (opacity)
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public TriAngleBean() {
        ByteBuffer data = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // (坐标数 * 4)float占四字节
        // 使用设备的本点字节序
        data.order(ByteOrder.nativeOrder());
        // 从ByteBuffer创建一个浮点缓冲
        vertexBuffer = data.asFloatBuffer();
        //把坐标们加入FloatBuffer中
        vertexBuffer.put(triangleCoords);
        //设置buffer，从第一个坐标开始读
        vertexBuffer.position(0);

        //编译shader代码
        int vertexShader = OpenGLActivity.MyGLRenderer.loadShader(GLES31.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = OpenGLActivity.MyGLRenderer.loadShader(GLES31.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // // 创建空的OpenGL ES Program
        mProgram = GLES31.glCreateProgram();

        // 将vertex shader(顶点着色器)添加到program
        GLES31.glAttachShader(mProgram, vertexShader);

        // 将fragment shader（片元着色器）添加到program
        GLES31.glAttachShader(mProgram, fragmentShader);

        // 创建可执行的 OpenGL ES program
        GLES31.glLinkProgram(mProgram);
    }

    public void draw(){
        // // 添加program到OpenGL ES环境中
        GLES31.glUseProgram(mProgram);

        // 获取指向vertex shader(顶点着色器)的成员vPosition的handle
        mPositionHandle = GLES31.glGetAttribLocation(mProgram, "vPosition");

        // 启用一个指向三角形的顶点数组的handle
        GLES31.glEnableVertexAttribArray(mPositionHandle);

        // 准备三角形的坐标数据
        GLES31.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES31.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // 获取指向fragment shader（片元着色器）的成员vColor的handle
        mColorHandle = GLES31.glGetUniformLocation(mProgram, "vColor");

        // 设置绘制三角形的颜色
        GLES31.glUniform4fv(mColorHandle, 1, color, 0);

        // 绘制三角形
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES31.glDisableVertexAttribArray(mPositionHandle);
    }
}
