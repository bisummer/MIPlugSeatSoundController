
package com.feng.soundcontroller;

import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    @Override
    public void run() {
        MyAccessibility.performClick();
    }

}
