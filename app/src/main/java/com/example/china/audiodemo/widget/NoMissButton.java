package com.example.china.audiodemo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.orhanobut.logger.Logger;

public class NoMissButton extends android.support.v7.widget.AppCompatButton {
    public NoMissButton(Context context) {
        super(context);
    }

    public NoMissButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoMissButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean callOnClick() {
        if (isFastClick()) {
            Logger.d("正常点击");
            return super.callOnClick();
        } else {
            Logger.d("异常点击");
            return true;
        }
    }

    // 两次点击按钮之间的点击间隔不能少于XXX毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}