
package com.feng.soundcontroller;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibility extends AccessibilityService {

    private static final String TAG = "MyAccessibility";

    private boolean isTimeout = true;

    public static double min = 10;

    static AccessibilityNodeInfo targetNode = null;

    public static void performClick() {
        Log.i(TAG, "call performClick...");
        if (targetNode != null && targetNode.isClickable()) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.i(TAG, "ACTION_CLICK");
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
