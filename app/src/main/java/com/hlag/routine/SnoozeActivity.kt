package com.hlag.routine

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_snooze.*


class SnoozeActivity : AppCompatActivity() {
    private var snoozeSet = false
    private var relevantStep: Step? = null
    private val routinePlayer = RoutinePlayer.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snooze)
        relevantStep = routinePlayer.activeStep

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        delay_min.requestFocus()
        delay_min.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    setSnooze(delay_min.text.toString().toInt()*60)
                    finish()
                    return true
                }
                return false
            }
        })
    }

    fun setSnooze(sec: Int){
        val app = routinePlayer
        app.beginStep(app.activeStep!!, sec)
        snoozeSet = true
    }

    override fun onPause() {
        super.onPause()
        if(!snoozeSet && routinePlayer.activeStep == relevantStep){
            setSnooze(60)
        }
        finish()
    }
}