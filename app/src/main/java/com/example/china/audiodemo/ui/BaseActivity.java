package com.example.china.audiodemo.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.Group.STORAGE,
                        Permission.Group.MICROPHONE,
                        Permission.Group.CAMERA)
                .start();
    }

    protected void showDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("正在初始化数据");
        dialog.setCancelable(false);
        dialog.show();
    }

    protected void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
