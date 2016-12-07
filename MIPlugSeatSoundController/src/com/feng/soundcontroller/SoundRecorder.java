
package com.feng.soundcontroller;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class SoundRecorder {

    private static final String TAG = "SoundRecorder";

    static final int SAMPLE_RATE_IN_HZ = 8000;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    AudioRecord mAudioRecord;
    boolean isGetVoiceRun;
    Object mLock;

    Timer timer;

    static double dBValue = 60;

    public static boolean firstClick = true;

    public static Queue<Long> queue = new LinkedList<Long>();

    public SoundRecorder() {
        mLock = new Object();
    }

    public void getNoiseLevel() {

        if (isGetVoiceRun) {
            Log.e(TAG, "is recording......");
            return;
        }

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        if (mAudioRecord == null) {
            Log.e("sound", "mAudioRecord init failed.");
        }

        isGetVoiceRun = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                short[] buffer = new short[BUFFER_SIZE];
                Log.i(TAG, "BUFFER_SIZE: " + BUFFER_SIZE);

                while (isGetVoiceRun) {
                    // r是实际读取的数据长度，一般而言r会小于buffersize
                    int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                    long v = 0;
                    // 将 buffer 内容取出，进行平方和运算
                    for (int i = 0; i < buffer.length; i++) {
                        v += buffer[i] * buffer[i];
                    }
                    // 平方和除以数据总长度，得到音量大小。
                    double mean = v / (double) r;
                    double volume = 10 * Math.log10(mean);
                    Log.d(TAG, "dB value: " + volume);

                    if (volume > dBValue) {

                        Log.e(TAG, "dB value: " + volume);
                        if (firstClick) {
                            Log.i(TAG, "first click");
                            MyAccessibility.performClick();
                            firstClick = false;
                        }

                        if (timer != null) {
                            Log.i(TAG, "cancel previous timer");
                            timer.cancel();
                        }

                        // long currentTime = System.currentTimeMillis();
                        // Log.i(TAG, "currentTime：" + currentTime);
                        // queue.offer(currentTime);

                        timer = new Timer();
                        timer.schedule(new MyTimerTask(), MyAccessibility.min
                                * 60000);
                        // timer.schedule(new MyTimerTask(), 6000);
                        Log.i(TAG, "new timer");
                    }

                    // 大概一秒十次
                    synchronized (mLock) {
                        try {
                            mLock.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }).start();
    }

}
