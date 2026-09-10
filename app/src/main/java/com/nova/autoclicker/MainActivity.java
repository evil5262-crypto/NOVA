package com.nova.autoclicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQ = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAccessibility = findViewById(R.id.btnAccessibility);
        Button btnOverlay = findViewById(R.id.btnOverlay);
        Button btnStart = findViewById(R.id.btnStart);

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
            startService(new Intent(this, FloatingButtonService.class));
            Toast.makeText(this, "NOVA روشن شد ✅", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
