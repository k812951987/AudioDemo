package com.example.china.audiodemo.ui;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.util.ByteBufferUtil;
import com.example.china.audiodemo.R;
import com.example.china.audiodemo.bean.WaveHeader;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioRecordActivity extends BaseActivity {
    @BindView(R.id.audio_bt)
    Button audioBt;
    @BindView(R.id.audio_wav_bt)
    Button audioWavBt;

    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    private static final int SAMPLE_RATE_INHZ = 44100;

    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private int minBufferSize;
    private AudioRecord audioRecord;
    private byte[] data;
    private boolean isRecording;
    private File audioFile;
    private String dir;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);
    }

    private void initData() {
        showDialog();
        minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

        data = new byte[minBufferSize];
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();   //获得sd卡路径
        dir = sdcardPath + File.separator + "测试" + File.separator;
        File file = new File(dir);
        if (!file.exists()) {                                     //如果不存在  就mkdirs()创建此文件夹
            file.mkdirs();
        }

        hideDialog();
    }

    @OnClick({R.id.audio_bt, R.id.play_stream, R.id.play_static, R.id.audio_wav_bt})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audio_bt:
                if (isRecording) {
                    stopRecord();
                    audioBt.setText("开始录音");
                } else {
                    initData();
                    startRecord();
                    audioBt.setText("停止录音");
                }
                break;
            case R.id.play_stream:
                playModeStream();
                break;
            case R.id.play_static:
                playModeStatic();
                break;

            case R.id.audio_wav_bt:
                if (isRecording) {
                    stopRecord();
                    audioWavBt.setText("WAV开始录音");
                } else {
                    initData();
                    startWavRecord();
                    audioWavBt.setText("WAV停止录音");
                }
                break;
        }

    }

    private WaveHeader header;

    private void startWavRecord() {
        audioFile = new File(dir + "test.wav");
        if (audioFile.exists()) {
            audioFile.delete();
        }
        audioRecord.startRecording();
        isRecording = true;
        header = new WaveHeader();
        header.setChannelMask((short) CHANNEL_CONFIG)
                .setBitPerSample((short) (AUDIO_FORMAT == AudioFormat.ENCODING_PCM_8BIT ? 8 : 16))
                .setSampleRate(SAMPLE_RATE_INHZ);


        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(audioFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (fos != null) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, minBufferSize);
                        Logger.d("录音保存大小" + minBufferSize);
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            addData(data);
                        }
                    }
                    try {
                        header.setDataLength(pcmData.length);
                        saveWaveFile(header);
                        fos.write(pcmData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    fos.close();
                    Logger.d("录音结束");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }).start();
    }


    /**
     * 保存wav标头文件
     *
     * @param header
     */
    private void saveWaveFile(WaveHeader header) {
        byte[] headBytes = header.getWaveHeader();
        byte[] tempBytes = new byte[pcmData.length + headBytes.length];
        System.arraycopy(headBytes, 0, tempBytes, 0, headBytes.length);
        System.arraycopy(pcmData, 0, tempBytes, headBytes.length, pcmData.length);
        pcmData = tempBytes;
    }

    private byte[] pcmData;

    /**
     * 合并byte数组
     *
     * @param bytes
     */
    public void addData(byte[] bytes) {
        Logger.d("新增数组" + bytes.length);
        if (pcmData == null) {
            pcmData = bytes;
        } else {
            byte[] tempBytes = new byte[pcmData.length + bytes.length];
            System.arraycopy(pcmData, 0, tempBytes, 0, pcmData.length);
            System.arraycopy(bytes, 0, tempBytes, pcmData.length, bytes.length);
            pcmData = tempBytes;
        }
        Logger.d("数组长度" + pcmData.length);
    }

    private void stopRecord() {
        isRecording = false;

        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }

    private void startRecord() {
        audioFile = new File(dir + "test.pcm");
        if (audioFile.exists()) {
            audioFile.delete();
        }
        audioRecord.startRecording();
        isRecording = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(audioFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (fos != null) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, minBufferSize);
                        Logger.d("录音保存大小" + minBufferSize);
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                fos.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
                try {
                    fos.close();
                    Logger.d("录音结束");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private AudioTrack audioTrack;
    private FileInputStream fis = null;

    @SuppressLint("WrongConstant")
    public void playModeStream() {
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        final int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT);

        audioTrack = new AudioTrack(
                new AudioAttributes.Builder().
                        setUsage(AudioAttributes.USAGE_MEDIA).
                        setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).
                        build(),
                new AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                        .setEncoding(AUDIO_FORMAT)
                        .setChannelMask(channelConfig)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
        );

        audioTrack.play();
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();   //获得sd卡路径
        String dir = sdcardPath + File.separator + "测试" + File.separator;
        File file = new File(dir + "test.pcm");
        if (!file.exists()) {
            Logger.d("文件不存在");
            return;
        }
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] tempBuffer = new byte[minBufferSize];
                    while (fis.available() > 0) {
                        int readCount = fis.read(tempBuffer);

                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                                readCount == AudioTrack.ERROR_BAD_VALUE) {
                            continue;
                        }

                        if (readCount != 0 && readCount != -1) {
                            audioTrack.write(tempBuffer, 0, readCount);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private byte[] audioData;

    public void playModeStatic() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                publishProgress(0);
                String sdcardPath = Environment.getExternalStorageDirectory().getPath();   //获得sd卡路径
                String dir = sdcardPath + File.separator + "测试" + File.separator;
                File file = new File(dir + "test.pcm");
                if (!file.exists()) {
                    Logger.d("文件不能为空");
                    return null;
                }
                try {
                    FileInputStream in = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int b;
                    while ((b = in.read()) != -1) {
                        Logger.d("当前流数据：" + b);
                        bos.write(b);
                    }
                    audioData = bos.toByteArray();


                    in.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                publishProgress(1);
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                switch (values[0]) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        hideDialog();
                        break;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (audioData == null) {
                    Logger.d("播放错误");
                    return;
                }
                audioTrack = new AudioTrack(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build(),
                        new AudioFormat.Builder()
                                .setSampleRate(SAMPLE_RATE_INHZ)
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build(),
                        audioData.length,
                        AudioTrack.MODE_STATIC,
                        AudioManager.AUDIO_SESSION_ID_GENERATE
                );

                audioTrack.write(audioData, 0, audioData.length);

                audioTrack.play();
            }
        }.execute();
    }
}
