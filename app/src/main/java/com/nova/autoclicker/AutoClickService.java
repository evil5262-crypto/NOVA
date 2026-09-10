package com.nova.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;

public class AutoClickService extends AccessibilityService {

    private static AutoClickService instance;

    private final Handler handler = new Handler(Looper.getMainLooper());

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

    /**
     * پیست + تأیید + ارسال، همه پشت سر هم
     */
    public void performAutoSend() {
        handler.post(() -> {
            android.util.DisplayMetrics metrics = getResources().getDisplayMetrics();
            int w = metrics.widthPixels;
            int h = metrics.heightPixels;

            // ۱. فشار طولانی روی فیلد چت (برای باز شدن منوی Paste)
            longPress(w * 0.38f, h * 0.92f, 600);
        });
    }

    /**
     * فشار طولانی
     */
    public void longPress(float x, float y, long duration) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(path, 0, duration);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(stroke)
                .build();

        dispatchGesture(gesture, null, null);
    }

    /**
     * کلیک ساده
     */
    public void tapAt(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(path, 0, 50);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(stroke)
                .build();

        dispatchGesture(gesture, null, null);
    }

    /**
     * زدن دکمه Send بازی
     */
    public void tapSendButton() {
        handler.post(() -> {
            android.util.DisplayMetrics metrics = getResources().getDisplayMetrics();
            int w = metrics.widthPixels;
            int h = metrics.heightPixels;

            // موقعیت دکمه Send توی Clash of Clans
            tapAt(w * 0.85f, h * 0.92f);
        });
    }

    @Override
    public void onInterrupt() { }

    @Override
    public boolean onUnbind(android.content.Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }
}
