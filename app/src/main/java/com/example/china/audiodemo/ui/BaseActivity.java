package com.example.china.audiodemo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

public abstract class BaseActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    protected ViewDataBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        initPermission();
        initViewAndData(savedInstanceState);
    }

    protected void initPermission(){
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.Group.STORAGE,
                        Permission.Group.MICROPHONE,
                        Permission.Group.CAMERA)
                .start();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    public abstract void initViewAndData(Bundle savedInstanceState);

    public abstract int getLayoutId();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }
}
