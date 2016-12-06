
package com.feng.soundcontroller;

import android.util.Log;

public class TimeQueueTask implements Runnable {

    private static final String TAG = "TimeQueueTask";

    @Override
    public void run() {

        while (true) {

            if (SoundRecorder.queue.size() >= 2) {
                long time1 = SoundRecorder.queue.poll();
                long time2 = SoundRecorder.queue.element();
                Log.i(TAG, "time1:" + time1);
                Log.i(TAG, "time2:" + time2);

                if (time2 - time1 > MyAccessibility.min * 60000) {
                    MyAccessibility.performClick();
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
