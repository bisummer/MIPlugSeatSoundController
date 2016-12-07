
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

    private static boolean enableKeyguard = true;// Ĭ������Ļ��

    // �������������
    private static KeyguardManager km;
    private static KeyguardLock kl;
    // ������Ļ���
    private static PowerManager pm;
    private static PowerManager.WakeLock wl = null;

    // ������Ļ�ͽ���
    private static void wakeAndUnlock(boolean unLock)
    {
        if (unLock)
        {
            // ��Ϊ����״̬������Ļ
            if (!pm.isScreenOn()) {
                // ��ȡ��Դ����������ACQUIRE_CAUSES_WAKEUP��������ܴӺ���������Ļ
                wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP, "bright");
                // ������Ļ
                wl.acquire();
                Log.i("demo", "����");
            }
            // �����������������ֱ����������
            if (km.inKeyguardRestrictedInputMode()) {
                // ���ý�����־�����ж��������ܷ�����
                enableKeyguard = false;
                // ����
                kl.disableKeyguard();
                Log.i("demo", "����");
            }
        }
        else
        {
            // ���֮ǰ�����������Իָ�ԭ��
            if (!enableKeyguard) {
                // ����
                kl.reenableKeyguard();
                Log.i("demo", "����");
            }
            // ��֮ǰ���ѹ���Ļ���ͷ�֮ʹ��Ļ�����ֳ���
            if (wl != null) {
                wl.release();
                wl = null;
                Log.i("demo", "�ص�");
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

        // ��ȡ��Դ����������
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        // �õ�����������������
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        // ��ʼ��һ������������������
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
