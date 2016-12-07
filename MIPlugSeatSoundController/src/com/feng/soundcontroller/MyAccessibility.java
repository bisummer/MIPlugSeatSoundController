
package com.feng.soundcontroller;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibility extends AccessibilityService {

    private static final String TAG = "MyAccessibility";

    private boolean isTimeout = true;

    public static long min = 10;

    static AccessibilityNodeInfo targetNode = null;

    private static boolean enableKeyguard = true;// 默认有屏幕锁

    // 锁屏、解锁相关
    private static KeyguardManager km;
    private static KeyguardLock kl;
    // 唤醒屏幕相关
    private static PowerManager pm;
    private static PowerManager.WakeLock wl = null;

    // 唤醒屏幕和解锁
    private static void wakeAndUnlock(boolean unLock)
    {
        if (unLock)
        {
            // 若为黑屏状态则唤醒屏幕
            if (!pm.isScreenOn()) {
                // 获取电源管理器对象，ACQUIRE_CAUSES_WAKEUP这个参数能从黑屏唤醒屏幕
                wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP, "bright");
                // 点亮屏幕
                wl.acquire();
                Log.i("demo", "亮屏");
            }
            // 若在锁屏界面则解锁直接跳过锁屏
            if (km.inKeyguardRestrictedInputMode()) {
                // 设置解锁标志，以判断抢完红包能否锁屏
                enableKeyguard = false;
                // 解锁
                kl.disableKeyguard();
                Log.i("demo", "解锁");
            }
        }
        else
        {
            // 如果之前解过锁则加锁以恢复原样
            if (!enableKeyguard) {
                // 锁屏
                kl.reenableKeyguard();
                Log.i("demo", "加锁");
            }
            // 若之前唤醒过屏幕则释放之使屏幕不保持常亮
            if (wl != null) {
                wl.release();
                wl = null;
                Log.i("demo", "关灯");
            }
        }
    }

    public static void performClick() {
        Log.i(TAG, "call performClick...");
        if (targetNode != null && targetNode.isClickable()) {
            wakeAndUnlock(true);
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.i(TAG, "ACTION_CLICK");
            wakeAndUnlock(false);
        }
    }

    @SuppressLint("NewApi")
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo,
            String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.i(TAG, "AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED triggered");
            AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
            targetNode = findNodeInfosById(nodeInfo, "com.xiaomi.plugseat:id/iv_switch");
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {

        // 获取电源管理器对象
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        // 得到键盘锁管理器对象
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        // 初始化一个键盘锁管理器对象
        kl = km.newKeyguardLock("unLock");

        AccessibilityServiceInfo info = getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        // listen for apps
        info.packageNames = new String[] {
                "com.xiaomi.smarthome", "com.xiaomi.smarthome:plugin", "com.xiaomi.plugseat"
        };
        setServiceInfo(info);
        super.onServiceConnected();
    }

}
