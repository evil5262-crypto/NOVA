package com.nova.autoclicker;

import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NovaKeyboardService extends InputMethodService {

    private EditText inputField;
    private final Handler handler = new Handler(Looper.getMainLooper());

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

            if (getCurrentInputConnection() != null) {
                getCurrentInputConnection().commitText(text, 1);
            }

            handler.postDelayed(() -> {
                sendKey(KeyEvent.KEYCODE_ENTER);

                handler.postDelayed(() -> {
                    AutoClickService service = AutoClickService.getInstance();
                    if (service != null) {
                        service.tapSendButton();
                    }
                }, 500);
            }, 200);

            inputField.setText("");
        });

        btnClose.setOnClickListener(v -> {
            hideWindow();
        });

        return view;
    }

    private void sendKey(int keyCode) {
        try {
            if (getCurrentInputConnection() != null) {
                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_UP, keyCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        if (inputField != null) {
            inputField.setText("");
        }
    }
}
