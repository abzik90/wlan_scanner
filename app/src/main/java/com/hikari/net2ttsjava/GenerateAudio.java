package com.hikari.net2ttsjava;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class GenerateAudio {
    double freqHz;
    int durationMs;
    final short buffer[] = new short[((int)44.1f*durationMs)];
    AudioTrack track;

    GenerateAudio(double freqHz, int durationMs){
        //Note that frequency is being divided by 2 by client's request
        this.durationMs = durationMs;
        this.freqHz = freqHz/2;
    }
    public void generateTone(){
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);
        track.write(buffer, 0, buffer.length);
        track.play();
    }
    public void stopTrack(String stopCode){
        stopCode = stopCode.replace("HTTP/1.1" , "").trim();
        if(stopCode.equals("true")) track.stop();
    }
}
