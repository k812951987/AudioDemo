package com.example.china.audiodemo.bean;

import com.orhanobut.logger.Logger;

public class WaveHeader {
    private String riff; // "RIFF"
    private int totalLength; //音频 data 数据长度 + 44 -8
    private String wave; // "WAVE"
    private String fmt; // "fmt "
    private int transition; //过渡字节，一般为0x00000010
    private short type; // PCM：1
    private short channelConfig; // 单声道：1，双声道：2
    private int sampleRateInHz; //采样率
    private int byteRate;  // sampleRateInHz * channelConfig * BitsPerSample / 8
    private short blockAlign; // channelConfig * BitsPerSample / 8 一个样本的字节数
    private short bitPerSample = 16; //位深度 8bit 16bit
    private String data; // "data"
    private int dataLength; //data数据长度

    public String getRiff() {
        return riff;
    }

    public WaveHeader setRiff(String riff) {
        this.riff = riff;
        return this;
    }

    public int getTotalLength() {
        if (dataLength != 0) {
            totalLength = dataLength + 44 - 8;
        }
        return totalLength;
    }

    public WaveHeader setTotalLength(int totalLength) {
        this.totalLength = totalLength;
        return this;
    }

    public String getWave() {
        return wave;
    }

    public WaveHeader setWave(String wave) {
        this.wave = wave;
        return this;
    }

    public String getFmt() {
        return fmt;
    }

    public WaveHeader setFmt(String fmt) {
        this.fmt = fmt;
        return this;
    }

    public int getTransition() {
        return transition;
    }

    public WaveHeader setTransition(int transition) {
        this.transition = transition;
        return this;
    }

    public short getType() {
        return type;
    }

    public WaveHeader setType(short type) {
        this.type = type;
        return this;
    }

    public short getChannelMask() {
        return channelConfig;
    }

    public WaveHeader setChannelMask(short channelConfig) {
        this.channelConfig = channelConfig;
        return this;
    }

    public int getSampleRate() {
        return sampleRateInHz;
    }

    public WaveHeader setSampleRate(int sampleRateInHz) {
        this.sampleRateInHz = sampleRateInHz;
        return this;
    }

    public int getByteRate() {
        byteRate = channelConfig * sampleRateInHz * bitPerSample / 8;
        return byteRate;
    }

    public WaveHeader setByteRate(int byteRate) {
        this.byteRate = byteRate;
        return this;
    }

    public short getBlockAlign() {
        blockAlign = (short) (channelConfig * bitPerSample / 8);
        return blockAlign;
    }

    public WaveHeader setBlockAlign(short blockAlign) {
        this.blockAlign = blockAlign;
        return this;
    }

    public short getBitPerSample() {
        return bitPerSample;
    }

    public WaveHeader setBitPerSample(short bitPerSample) {
        this.bitPerSample = bitPerSample;
        return this;
    }

    public String getData() {
        return data;
    }

    public WaveHeader setData(String data) {
        this.data = data;
        return this;
    }

    public int getDataLength() {
        return dataLength;
    }

    public WaveHeader setDataLength(int dataLength) {
        this.dataLength = dataLength;
        return this;
    }

    public byte[] getWaveHeader() {
        byte[] header = new byte[44];
        //"RIFF" 字符串 chunkId
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        if (dataLength == 0) {
            Logger.d("PCM data长度不能为0");
            return null;
        }

        //下个地址后续长度  data总长度+ 44标头长度 -8当前长度
        header[4] = (byte) (getTotalLength() & 0xff);
        header[5] = (byte) ((getTotalLength() >> 8) & 0xff);
        header[6] = (byte) ((getTotalLength() >> 16) & 0xff);
        header[7] = (byte) ((getTotalLength() >> 24) & 0xff);

        //WAVE 字符串
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        //fmt  字符串
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        //16用于PCM
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;

//        PCM = 1
//        除1以外的值表示一些值
//        压缩的形式
        header[20] = 1;
        header[21] = 0;

        //单声道1 立体声2等
        header[22] = 1;
        header[23] = 0;

        //8000、44100
        if (sampleRateInHz == 0) {
            Logger.d("采样率Khz不能为0");
            return null;
        }
        header[24] = (byte) (sampleRateInHz & 0xff);
        header[25] = (byte) ((sampleRateInHz >> 8) & 0xff);
        header[26] = (byte) ((sampleRateInHz >> 16) & 0xff);
        header[27] = (byte) ((sampleRateInHz >> 24) & 0xff);

        if (channelConfig == 0) {
            Logger.d("通道数不能为0");
            return null;
        }
        if (bitPerSample == 0) {
            Logger.d("位深度不能为0");
            return null;
        }
        //字节速率 byteRate
        header[28] = (byte) (getByteRate() & 0xff);
        header[29] = (byte) ((getByteRate() >> 8) & 0xff);
        header[30] = (byte) ((getByteRate() >> 16) & 0xff);
        header[31] = (byte) ((getByteRate() >> 24) & 0xff);

        //块状排列 block align
        header[32] = (byte) getBlockAlign();
        header[33] = 0;

        //位深度 8BIT 16BIT
        header[34] = (byte) bitPerSample;
        header[35] = 0;

        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        //DATA数据长度
        header[40] = (byte) (dataLength & 0xff);
        header[41] = (byte) ((dataLength >> 8) & 0xff);
        header[42] = (byte) ((dataLength >> 16) & 0xff);
        header[43] = (byte) ((dataLength >> 24) & 0xff);
        return header;
    }
}
