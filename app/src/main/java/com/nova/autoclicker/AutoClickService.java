package com.nova.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Arrays;
import java.util.List;

public class AutoClickService extends AccessibilityService {

    private static AutoClickService instance;

    private static final List<String> TARGET_TEXTS = Arrays.asList(
            "ارسال", "تایید", "تأیید", "پیست", "چسباندن", "OK",
            "Send", "Confirm", "Paste"
    );

    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final long STEP_DELAY = 20;

    public static AutoClickService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    public int findAndClickAll() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return 0;

        int clicked = 0;

        for (String text : TARGET_TEXTS) {
            List<AccessibilityNodeInfo> nodes =
                    root.findAccessibilityNodeInfosByText(text);

            if (nodes == null || nodes.isEmpty()) continue;

            for (AccessibilityNodeInfo node : nodes) {
                if (node == null) continue;

                if (clickNode(node)) {
                    clicked++;
                    sleep(STEP_DELAY);
                }
            }
        }
        return clicked;
    }

    private boolean clickNode(AccessibilityNodeInfo node) {
        if (node.isClickable()) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        AccessibilityNodeInfo parent = node.getParent();
        while (parent != null) {
            if (parent.isClickable()) {
                return parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            AccessibilityNodeInfo next = parent.getParent();
            parent.recycle();
            parent = next;
        }
        return false;
    }

    public void tapAt(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(path, 0, 1);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(stroke)
                .build();

        dispatchGesture(gesture, null, null);
    }

    public void performSequence() {
        handler.post(() -> {
            clickByText("پیست", "Paste", "چسباندن");
            sleep(STEP_DELAY);
            clickByText("تایید", "تأیید", "OK", "Confirm");
            sleep(STEP_DELAY);
            clickByText("ارسال", "Send", "Send Message");
        });
    }

    private void clickByText(String... texts) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        for (String text : texts) {
            List<AccessibilityNodeInfo> nodes =
                    root.findAccessibilityNodeInfosByText(text);
            if (nodes == null || nodes.isEmpty()) continue;

            for (AccessibilityNodeInfo node : nodes) {
                if (clickNode(node)) return;
            }
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    @Override
    public void onInterrupt() { }

    @Override
    public boolean onUnbind(android.content.Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }
}
