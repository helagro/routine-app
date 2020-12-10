package com.hlag.routine;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.graphics.ColorUtils;

public class TextDialog extends Dialog {
    TextDialog(Context context) {
        super(context);

        show();
    }

    public EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_input_dialog);
        final Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        editText = findViewById(R.id.name_dialog_edittext);
        editText.requestFocus();
        final Button doneBtn = findViewById(R.id.name_dialog_done);
        final Button cancelBtn = findViewById(R.id.name_dialog_cancel);

        doneBtn.setOnClickListener(view -> {
            dismiss();
        });

        cancelBtn.setOnClickListener(view -> dismiss());
    }

}
