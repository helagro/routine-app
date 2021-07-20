package com.hlag.routine

import android.os.CountDownTimer
import java.util.*

class RoutinePlayer {
    companion object {
        private var instance: RoutinePlayer? = null
        fun getInstance(): RoutinePlayer {
            instance = instance ?: RoutinePlayer()
            return instance!!
        }
    }

    var isRunning = false
    lateinit var routine: Routine
    private var timeStarted = -1L
    var activeStep: Step? = null
    lateinit var timerListener: TimerListeners
    private var timer: CountDownTimer? = null
    var overDue = false

    interface TimerListeners {
        fun everySecond(secsLeft: Int)
        fun onStarted()
        fun onStepTimerFinished()
        fun onRoutineFinished()
    }

    fun startRoutine(){
        timeStarted = Calendar.getInstance().timeInMillis
        nextStep()
        isRunning = true
        timerListener.onStarted()
    }

    fun beginStep(step: Step, duration: Int) {
        timer?.cancel()

        overDue = false
        activeStep = step

        if (duration != 0) startTimer(duration)
    }

    private fun startTimer(duration: Int) {
        timer = object : CountDownTimer(duration * 1000L, 1000) {

            override fun onTick(p0: Long) {
                timerListener.everySecond((p0 / 1000).toInt())
            }

            override fun onFinish() {
                if (activeStep?.duration != 0) {
                    overDue = true
                    timerListener.onStepTimerFinished()
                }
            }
        }

        timer!!.start()
    }

    fun nextStep() {
        activeStep?.checked = true

        //get and start next
        activeStep = routine.getNext()
        if (activeStep == null) {
            finishRoutine()
        } else {
            beginStep(activeStep!!, activeStep!!.duration)
        }
    }

    fun finishRoutine() {
        timer?.cancel()
        activeStep = null
        isRunning = false

        uncheckAllSteps()
        timerListener.onRoutineFinished()
        //calculateRunLength()
    }

    private fun calculateRunLength() {
        val timeDiff = Calendar.getInstance().timeInMillis - timeStarted
        val minDiff = (timeDiff / 1000).toInt()

        //Toast.makeText(applicationContext, "Routine time duration " + GeneralHelpers.secToStr(minDiff), Toast.LENGTH_LONG).show()
    }

    fun uncheckAllSteps() {
        routine.steps.forEachIndexed { _, step ->
            step.checked = false
        }
    }
}