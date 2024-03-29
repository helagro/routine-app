package com.hlag.routine

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class GeneralHelpers {
    companion object {
        fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        fun startForeground(context: Context, serviceClass: Class<*>, action: String) {
            val intent = Intent(context, RoutineService::class.java)
            intent.action = action
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun secToStr(sec: Int): String {
            val minutes: Int = sec / (60)
            val seconds: Int = sec % 60
            return String.format("%d:%02d", minutes, seconds)
        }
    }
}