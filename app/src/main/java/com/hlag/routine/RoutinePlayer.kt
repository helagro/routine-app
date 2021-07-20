package com.hlag.routine

import android.os.CountDownTimer
import android.widget.Toast
import java.util.*

class RoutinePlayer {
    companion object{
        private var instance : RoutinePlayer? = null;
        fun getInstance() : RoutinePlayer{
            instance = instance ?: RoutinePlayer()
            return instance!!
        }
    }

    lateinit var activeRoutine: Routine
    var timeStarted = -1L
    var activeStep: Step? = null
    lateinit var timerListener: TimerListeners
    private var timer: CountDownTimer? = null
    var timed = false
    var overDue = false

    private fun RoutinePlayer(): RoutinePlayer{

        return this
    }

    interface TimerListeners {
        abstract fun everySecond(secsLeft: Int)
        fun onFinished()
        fun onAllStepsFinished()
    }

    fun startTimer(step: Step, duration: Int) {
        if(timer == null){
            timeStarted = Calendar.getInstance().timeInMillis
        } else{
            timer!!.cancel()
        }

        overDue = false

        timer = object : CountDownTimer(duration * 1000L, 1000) {

            override fun onTick(p0: Long) {
                timerListener.everySecond((p0 / 1000).toInt())
            }

            override fun onFinish() {
                if(activeStep?.duration != 0){
                    overDue = true
                    timerListener.onFinished()
                }
            }
        }

        timer!!.start()
        timed = true
        activeStep = step
    }

    fun nextStep(){
        activeStep?.checked = true

        //get and start next
        activeStep = activeRoutine.getNext()
        if(activeStep == null){
            stopTimer()
        }
        else{
            startTimer(activeStep!!, activeStep!!.duration)
        }
    }

    fun stopTimer(){
        timer?.cancel()
        timerListener.onAllStepsFinished()
        timed = false
        activeStep = null

        //calculateRunLength()
    }

    private fun calculateRunLength(){
        val timeDiff = Calendar.getInstance().timeInMillis - timeStarted
        val minDiff = (timeDiff / 1000).toInt()

        //Toast.makeText(applicationContext, "Routine time duration " + GeneralHelpers.secToStr(minDiff), Toast.LENGTH_LONG).show()
    }
}