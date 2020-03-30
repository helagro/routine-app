package com.hlag.routine

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

class RoutineService : Service() {
    val TAG = "RoutineService"

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null || intent.action == null){
            return super.onStartCommand(intent, flags, startId)
        }

        when (intent.action) {
            "START" -> "dw"
        }

        Log.d(TAG, intent.action)

        return super.onStartCommand(intent, flags, startId)
    }




    /*
    commands
        onpause -> timer running? start shit : destroy
        onresume -> running: pause
     */
}