package com.example.china.audiodemo.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.china.audiodemo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @BindView(R.id.main_list)
    RecyclerView mainView;

    private ArrayList<ItemClass> list = new ArrayList<>();

    private MainAdapter adapter;

    public class ItemClass {
        public ItemClass(Class aClass, String name) {
            this.aClass = aClass;
            this.name = name;
        }

        private Class aClass;

        private String name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        list.add(new ItemClass(ImageActivity.class, "1、图片展示形式"));
        list.add(new ItemClass(AudioRecordActivity.class, "2、PCM、WAV语音录制"));
        list.add(new ItemClass(CameraActivity.class, "3、Camera预览"));
        list.add(new ItemClass(MP4Activity.class, "4、音视频解析、合成"));
        list.add(new ItemClass(OpenGLActivity.class, "5、OpenGL ES 绘制"));

        adapter.notifyDataSetChanged();
    }

    private void initView() {
        adapter = new MainAdapter();
        mainView.setLayoutManager(new LinearLayoutManager(this));
        mainView.setAdapter(adapter);
    }


    public class MainAdapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_layout, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            holder.setIsRecyclable(false);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            final ItemClass item = list.get(i);
            holder.nameTv.setText(item.name);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, item.aClass));
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        public TextView nameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
