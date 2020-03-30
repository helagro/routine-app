package com.hlag.routine

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.step_edit_dialog.*

class StepEditDialog(context: Context) : Dialog(context) {
    init {
        show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step_edit_dialog)

    }
}