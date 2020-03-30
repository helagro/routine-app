package com.hlag.routine

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.CountDownTimer
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.sql.Time


class MyApp : Application() {

    override fun onCreate() {

        super.onCreate()
    }

    companion object {
        var dir = "/storage/emulated/0/Mega Sync/Routines"

        fun writeRoutine(context: Context, routine: Routine){
            val text = Gson().toJson(routine).toString()
            val file = File(dir, routine.name + ".txt")

            val fileOutputStream = FileOutputStream(file)
            try {
                fileOutputStream.write(text.toByteArray())
            } finally {
                fileOutputStream.close()
            }
        }
    }


    lateinit var activeRoutine: Routine

    lateinit var timerListener: TimerListeners
    private lateinit var timer: CountDownTimer

    interface TimerListeners {
        abstract fun everySecond(secsLeft: Int)
        abstract fun onFinish()
    }


    fun startTimer(duration: Int){
        timer = object: CountDownTimer(duration*1000L, 1000) {

            override fun onTick(p0: Long) {
                timerListener.everySecond((p0/1000).toInt())
            }


            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }
        timer.start()
    }

}