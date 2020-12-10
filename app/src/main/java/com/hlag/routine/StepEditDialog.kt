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

    var delete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step_edit_dialog)

        step_delete.setOnClickListener {
            delete = true
            dismiss()
        }
    }

    public fun getDuration(): Int{
        return step_minutes_edit.text.toString().toInt() * 60 + step_seconds_edit.text.toString().toInt()
    }
}