package com.hlag.routine

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.ColorSpace
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class RoutineService : Service(), MyApp.TimerListeners {
    val TAG = "RoutineService"
    val COMPLETE_ACTION = "complete-action"

    val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            (application as MyApp).nextStep()
            builder?.color = -16711921
            builder?.setOnlyAlertOnce(true)
        }

    }

    var builder: NotificationCompat.Builder? = null
    var mNotificationManager: NotificationManager? = null
    var TIMER_ID = 0


    override fun onCreate() {
        super.onCreate()

        //receiver
        val filter = IntentFilter()
        filter.addAction(COMPLETE_ACTION)
        registerReceiver(notificationReceiver, filter)


        //notification
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notiIntent = Intent(applicationContext, StepsActivity::class.java)
        val completePendingIntent = PendingIntent.getBroadcast(this, 0, Intent().setAction(COMPLETE_ACTION), 0)

        builder = NotificationCompat.Builder(applicationContext, MyApp.ROUTINE_PLAYER)
            .setSmallIcon(R.drawable.ic_add_black_24dp)
            .setContentTitle("Title")
            .setContentText("Text")
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_check_box_outline_blank_black_24dp, "Complete", completePendingIntent)
            .setContentIntent(PendingIntent.getActivity(applicationContext, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(true)
            .setColor(-16711921)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null || intent.action == null){
            return super.onStartCommand(intent, flags, startId)
        }

        when (intent.action) {
            "START" -> {
                (application as MyApp).timerListener = this
                startForeground(TIMER_ID, builder?.build())
            }
            "PAUSE" -> {
                stopForeground(true)
                mNotificationManager?.cancel(TIMER_ID)
            }
            "DESTROY" -> stopSelf()
        }

        Log.d(TAG, intent.action)

        return super.onStartCommand(intent, flags, startId)
    }



    override fun everySecond(secsLeft: Int) {
        builder?.setContentText(secsLeft.toString())
        mNotificationManager!!.notify(TIMER_ID, builder!!.build())
    }

    override fun onNext() {
        builder?.setContentTitle((application as MyApp).activeStep!!.text)
        mNotificationManager!!.notify(TIMER_ID, builder!!.build())
    }

    override fun onFinished() {
        builder?.setColor(-60892)?.setOnlyAlertOnce(false)
        mNotificationManager!!.notify(TIMER_ID, builder!!.build())
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }
}