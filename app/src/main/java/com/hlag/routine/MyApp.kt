package com.hlag.routine

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import java.util.*


class MyApp : Application() {

    companion object {
        const val ERROR_CHAN = "Errors"
        const val ROUTINE_PLAYER = "Routine player"

        fun getSp(context: Context): SharedPreferences {
            return Objects.requireNonNull(context)
                .getSharedPreferences("prefs", Activity.MODE_PRIVATE)
        }
    }


    override fun onCreate() {
        super.onCreate()

        val sp = getSp(this)
        FileManager.dir = sp.getString("prjDir", "/storage/emulated/0/0doc/mega-sync/routines").toString()

        //first setup
        if (sp.getBoolean("first", true)) {  //only debug
            firstSetup()
            sp.edit().putBoolean("first", false).apply()
        }
    }

    private fun firstSetup() {
        //channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val names = arrayOf<String>(
                ERROR_CHAN,
                ROUTINE_PLAYER
            )
            val priorities = intArrayOf(
                NotificationManager.IMPORTANCE_HIGH, NotificationManager.IMPORTANCE_LOW
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




}