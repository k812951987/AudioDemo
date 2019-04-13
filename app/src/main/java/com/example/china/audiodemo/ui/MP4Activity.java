package com.example.china.audiodemo.ui;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.example.china.audiodemo.R;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MP4Activity extends BaseActivity {
    //视频通道数
    private int trackCount;
    //具体通道
    private int videoIndex = -1;
    private int audioIndex = -1;
    private String dir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4);
        ButterKnife.bind(this);
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();   //获得sd卡路径
        dir = sdcardPath + File.separator + "测试" + File.separator;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @OnClick({R.id.video_parse, R.id.audio_parse, R.id.audio_video_bt})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_parse:
                videoParse();
                break;
            case R.id.audio_parse:
                audioParse();
                break;
            case R.id.audio_video_bt:
                try {
                    combineVideo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 音视频合成
     */
    private void combineVideo() throws IOException {
        String audioPath = dir + "out_audio.aac";
        String videoPath = dir + "out_video.mp4";
        File audioFile = new File(audioPath);
        File videoFile = new File(videoPath);
        if (!audioFile.exists()) {
            Toast.makeText(this, "音频文件不存在", Toast.LENGTH_LONG).show();
        }
        if (!videoFile.exists()) {
            Toast.makeText(this, "视频文件不存在", Toast.LENGTH_LONG).show();
        }

        MediaExtractor videoExtract = new MediaExtractor();
        videoExtract.setDataSource(videoPath);
        MediaFormat videoFormat = null;
        int videoTrackIndex = -1;
        int videoTractCount = videoExtract.getTrackCount();
        for (int i = 0; i < videoTractCount; i++) {
            videoFormat = videoExtract.getTrackFormat(i);
            String videoMime = videoFormat.getString(MediaFormat.KEY_MIME);
            if (videoMime.startsWith("video/")) {
                videoTrackIndex = i;
            }
        }

        MediaExtractor audioExtract = new MediaExtractor();
        audioExtract.setDataSource(audioPath);
        MediaFormat audioFormat = null;
        int audioTrackIndex = -1;
        int audioTractCount = audioExtract.getTrackCount();
        for (int i = 0; i < audioTractCount; i++) {
            audioFormat = audioExtract.getTrackFormat(i);
            String audioMime = audioFormat.getString(MediaFormat.KEY_MIME);
            if (audioMime.startsWith("audio/")) {
                audioTrackIndex = i;
            }
        }

        videoExtract.selectTrack(videoTrackIndex);
        audioExtract.selectTrack(audioTrackIndex);

        ByteBuffer videoBuffer = ByteBuffer.allocate(1024 * 1024);
        ByteBuffer audioBuffer = ByteBuffer.allocate(1024 * 1024);

        MediaMuxer mediaMuxer = new MediaMuxer(dir + "Test.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        int videoWriterIndex = mediaMuxer.addTrack(videoFormat);
        int audioWriterIndex = mediaMuxer.addTrack(audioFormat);

        MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();
        mediaMuxer.start();

        long videoSampleTime = 0;
        {
            videoExtract.readSampleData(videoBuffer, 0);
            if (videoExtract.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC)
                videoExtract.advance();

            videoExtract.readSampleData(videoBuffer, 0);
            long videoSampleTime1 = videoExtract.getSampleTime();
            videoExtract.advance();

            videoExtract.readSampleData(videoBuffer, 0);
            long videoSampleTime2 = videoExtract.getSampleTime();
            videoSampleTime = Math.abs(videoSampleTime1 - videoSampleTime2);

            videoExtract.unselectTrack(videoTrackIndex);
            videoExtract.selectTrack(videoTrackIndex);
        }

        long audioSampleTime = 0;
        {
            audioExtract.readSampleData(audioBuffer, 0);
            if (audioExtract.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                audioExtract.advance();
            }
            long audioSampleTime1 = 0;
            audioExtract.readSampleData(audioBuffer, 0);
            audioSampleTime1 = audioExtract.getSampleTime();
            audioExtract.advance();

            long audioSampleTime2 = 0;
            audioExtract.readSampleData(audioBuffer, 0);
            audioSampleTime2 = audioExtract.getSampleTime();

            audioSampleTime = Math.abs(audioSampleTime1 - audioSampleTime2);
            audioExtract.unselectTrack(audioTrackIndex);
            audioExtract.selectTrack(audioTrackIndex);
        }

        while (true) {
            int data = videoExtract.readSampleData(videoBuffer, 0);
            if (0 > data) {
                break;
            }

            videoInfo.offset = 0;
            videoInfo.flags = videoExtract.getSampleFlags();
            videoInfo.size = data;
            videoInfo.presentationTimeUs += videoSampleTime;

            mediaMuxer.writeSampleData(videoWriterIndex, videoBuffer, videoInfo);
            videoExtract.advance();
        }

        while (true){
            int data = audioExtract.readSampleData(audioBuffer, 0);
            if (0 > data) {
                break;
            }

            audioInfo.offset = 0;
            audioInfo.flags = audioExtract.getSampleFlags();
            audioInfo.size = data;
            audioInfo.presentationTimeUs += audioSampleTime;

            mediaMuxer.writeSampleData(audioWriterIndex, audioBuffer, audioInfo);
            audioExtract.advance();
        }

        mediaMuxer.stop();
        mediaMuxer.release();
        videoExtract.release();
        audioExtract.release();
    }


    /**
     * 音频解析
     */
    private void audioParse() {
        String outVideoPath = dir + "out_audio.aac";
        File file = new File(outVideoPath);
        if (file.exists()) {
            file.delete();
        }
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            //设置数据源
            mediaExtractor.setDataSource(getResources().openRawResourceFd(R.raw.input));
            trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mime = trackFormat.getString(MediaFormat.KEY_MIME);
                Logger.d("mime:" + mime);
                //循环拿到音频通道
                if (mime.startsWith("audio/")) {
                    audioIndex = i;
                    Logger.d("audioIndex:" + audioIndex);
                }
            }
            //切换到音频通道
            mediaExtractor.selectTrack(audioIndex);
            //拿到音频通道format
            MediaFormat format = mediaExtractor.getTrackFormat(audioIndex);
            Logger.d(format.toString());

            //输出路径 视频合成器
            MediaMuxer mediaMuxer = new MediaMuxer(outVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeAudio = mediaMuxer.addTrack(format);

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();

            //每一帧的时间
            long audioSametime = 0;

            /**
             * 获取音频单帧时长
             */
            {
                mediaExtractor.readSampleData(buffer, 0);
                //跳过I帧，要P帧（视频是由个别I帧和很多P帧组成）
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_PARTIAL_FRAME) {
                    mediaExtractor.advance();
                }

                mediaExtractor.readSampleData(buffer, 0);
                long sampleTime = mediaExtractor.getSampleTime();
                Logger.d("第一帧时长" + sampleTime);
                mediaExtractor.advance();

                mediaExtractor.readSampleData(buffer, 0);
                long sampleTime1 = mediaExtractor.getSampleTime();
                Logger.d("第二帧时长" + sampleTime1);
                audioSametime = Math.abs(sampleTime - sampleTime1);
            }


            mediaExtractor.unselectTrack(audioIndex);
            mediaExtractor.selectTrack(audioIndex);

            while (true) {
                int data = mediaExtractor.readSampleData(buffer, 0);
                if (data < 0) {
                    break;
                }
                bufferInfo.size = data;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += audioSametime;
                Logger.d("writer:" + audioIndex);

                mediaMuxer.writeSampleData(writeAudio, buffer, bufferInfo);
                mediaExtractor.advance();
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();

            Logger.d("总通道数:" + trackCount + "__音频通道Index:" + writeAudio + "__音频单帧时长:" + audioSametime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 视频解析
     */
    public void videoParse() {
        String outVideoPath = dir + "out_video.mp4";
        File file = new File(outVideoPath);
        if (file.exists()) {
            file.delete();
        }

        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            //设置数据源
            mediaExtractor.setDataSource(getResources().openRawResourceFd(R.raw.input));
            trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mime = trackFormat.getString(MediaFormat.KEY_MIME);
                //循环拿到视频通道
                if (mime.startsWith("video/")) {
                    videoIndex = i;
                }


            }
            //切换到视频通道
            mediaExtractor.selectTrack(videoIndex);
            //拿到视频通道format
            MediaFormat format = mediaExtractor.getTrackFormat(videoIndex);
            //输出路径 视频合成器
            MediaMuxer mediaMuxer = new MediaMuxer(outVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mediaMuxer.addTrack(format);

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();

            //每一帧的时间
            long videoSametime = 0;

            /**
             * 获取视频帧率
             */
            {

                mediaExtractor.readSampleData(buffer, 0);
                //跳过I帧，要P帧（视频是由个别I帧和很多P帧组成）
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_PARTIAL_FRAME) {
                    mediaExtractor.advance();
                }

                mediaExtractor.readSampleData(buffer, 0);
                long sampleTime = mediaExtractor.getSampleTime();
                Logger.d("第一帧时长" + sampleTime);
                mediaExtractor.advance();

                mediaExtractor.readSampleData(buffer, 0);
                long sampleTime1 = mediaExtractor.getSampleTime();
                Logger.d("第二帧时长" + sampleTime1);
                videoSametime = Math.abs(sampleTime - sampleTime1);
            }


            mediaExtractor.unselectTrack(videoIndex);
            mediaExtractor.selectTrack(videoIndex);

            while (true) {
                int data = mediaExtractor.readSampleData(buffer, 0);
                if (data < 0) {
                    break;
                }

                bufferInfo.size = data;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += videoSametime;
                mediaMuxer.writeSampleData(videoIndex, buffer, bufferInfo);
                mediaExtractor.advance();
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();

            Logger.d("总通道数:" + trackCount + "__视频通道Index:" + videoIndex + "__视频单帧时长:" + videoSametime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
