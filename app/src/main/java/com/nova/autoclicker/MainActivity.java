package com.nova.autoclicker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQ = 1001;
    public static final String PREFS = "nova_prefs";
    public static final String KEY_MESSAGE = "message_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etMessage = findViewById(R.id.etMessage);
        Button btnAccessibility = findViewById(R.id.btnAccessibility);
        Button btnOverlay = findViewById(R.id.btnOverlay);
        Button btnStart = findViewById(R.id.btnStart);

        // بارگذاری متن ذخیره‌شده
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        etMessage.setText(prefs.getString(KEY_MESSAGE, ""));

        btnAccessibility.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            Toast.makeText(this, "NOVA را فعال کن", Toast.LENGTH_LONG).show();
        });

        btnOverlay.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ);
                } else {
                    Toast.makeText(this, "اجازه از قبل داده شده", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnStart.setOnClickListener(v -> {
            // ذخیره متن
            String msg = etMessage.getText().toString();
            if (msg.trim().isEmpty()) {
                Toast.makeText(this, "اول متن پیام رو وارد کن", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString(KEY_MESSAGE, msg).apply();

            startService(new Intent(this, FloatingButtonService.class));
            Toast.makeText(this, "NOVA روشن شد ✅", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
