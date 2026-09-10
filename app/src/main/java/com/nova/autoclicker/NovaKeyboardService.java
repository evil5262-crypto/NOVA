package com.nova.autoclicker;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NovaKeyboardService extends InputMethodService {

    private EditText inputField;

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.keyboard_layout, null);

        inputField = view.findViewById(R.id.keyboardInput);
        Button btnSend = view.findViewById(R.id.keyboardSend);
        Button btnClose = view.findViewById(R.id.keyboardClose);

        btnSend.setOnClickListener(v -> {
            String text = inputField.getText().toString();
            if (text.trim().isEmpty()) {
                Toast.makeText(this, "متن خالی است", Toast.LENGTH_SHORT).show();
                return;
            }

            // متن رو توی کلیپ‌بورد کپی کن
            android.content.ClipboardManager clipboard =
                    (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip =
                    android.content.ClipData.newPlainText("nova", text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }

            // حلقه رو شروع کن
            AutoClickService service = AutoClickService.getInstance();
            if (service != null) {
                service.startAutoSendLoop();
            }

            Toast.makeText(this, "NOVA شروع کرد ⚡", Toast.LENGTH_SHORT).show();
        });

        btnClose.setOnClickListener(v -> {
            AutoClickService service = AutoClickService.getInstance();
            if (service != null) {
                service.stopAutoSendLoop();
            }
            hideWindow();
        });

        return view;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        if (inputField != null) {
            String savedText = getSharedPreferences("nova_prefs", MODE_PRIVATE)
                    .getString("message_text", "");
            inputField.setText(savedText);
        }
    }
}
