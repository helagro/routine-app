package com.hlag.routine

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*


class MyApp : Application() {

    companion object {
        val ERROR_CHAN = "Errors"
        val ROUTINE_PLAYER = "Routine player"
        var dir = "/storage/emulated/0/Mega Sync/Routines"

        fun getSp(context: Context): SharedPreferences {
            return Objects.requireNonNull(context)
                .getSharedPreferences("prefs", Activity.MODE_PRIVATE)
        }

        fun writeRoutine(context: Context, routine: Routine) {
            val text = Gson().toJson(routine).toString()
            val file = File(dir, routine.name + ".txt")

            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.use { fileOutputStream ->
                fileOutputStream.write(text.toByteArray())
            }
        }
    }


    override fun onCreate() {
        super.onCreate()

        //first setup
        val sp = getSp(this)
        if (sp.getBoolean("first", true) || true) {  //only debugg
            firstSetup()
            sp.edit().putBoolean("first", false).apply()
        }
    }

    fun firstSetup() {
        //channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val names = arrayOf<String>(
                ERROR_CHAN,
                ROUTINE_PLAYER
            )
            val priorities = intArrayOf(
                NotificationManager.IMPORTANCE_HIGH, NotificationManager.IMPORTANCE_HIGH
            )
            val descriptions = arrayOf(
                "For showing errors", "To see steps outside the app"
            )
            for (i in names.indices) {
                val channel =
                    NotificationChannel(names[i], names[i], priorities[i])
                channel.description = descriptions[i]
                val notificationManager = getSystemService( NotificationManager::class.java )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }


    lateinit var activeRoutine: Routine
    var activeStep: Step? = null

    lateinit var timerListener: TimerListeners
    private lateinit var timer: CountDownTimer
    var timerRunning = false

    interface TimerListeners {
        abstract fun everySecond(secsLeft: Int)
        fun onNext()
        fun onFinished()
    }

    fun startTimer(duration: Int) {
        if(timerRunning){
            timer.cancel()
        }

        timer = object : CountDownTimer(duration * 1000L, 1000) {

            override fun onTick(p0: Long) {
                timerListener.everySecond((p0 / 1000).toInt())
            }


            override fun onFinish() {
                timerRunning = false
                timerListener.onFinished()
            }
        }
        timer.start()
        timerRunning = true
    }

    fun nextStep(){
        activeStep?.let {
            activeStep!!.checked = true
        }

        //get and start next
        activeStep = activeRoutine.getNext()
        timerListener.onNext()
        if(activeStep == null){
            return
        }
        startTimer(activeStep!!.duration)
    }

}