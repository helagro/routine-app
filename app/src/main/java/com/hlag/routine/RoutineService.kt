package com.hlag.routine

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*


class RoutineService : Service(), MyApp.TimerListeners {
    companion object{
        val TAG = "RoutineService"
        val COMPLETE_ACTION = "complete-action"
    }

    private val TIMER_ID = 1 //can't be 0 for some reason
    private val OVERDUE_TIMER_DELAY = 5*60*10L

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) { //complete button
            val application = (application as MyApp)
            application.nextStep()

            application.activeStep?.let {
                updateNotificationStep()

            } ?: run {//Done all steps
                stopSelf()
            }
        }
    }

    lateinit var vibrator: Vibrator
    var builder: NotificationCompat.Builder? = null
    var mNotificationManager: NotificationManager? = null
    lateinit var app: MyApp


    override fun onCreate() {
        super.onCreate()

        app = application as MyApp
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        //receiver
        val filter = IntentFilter()
        filter.addAction(COMPLETE_ACTION)
        registerReceiver(notificationReceiver, filter)


        //notification
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notiIntent = Intent(applicationContext, StepsActivity::class.java)
        val completePendingIntent =
            PendingIntent.getBroadcast(this, 0, Intent().setAction(COMPLETE_ACTION), 0)

        builder = NotificationCompat.Builder(applicationContext, MyApp.ROUTINE_PLAYER)
            .setSmallIcon(R.drawable.ic_add_black_24dp)
            .setContentText("Text")
            .addAction(
                R.drawable.ic_check_box_outline_blank_black_24dp,
                "Complete",
                completePendingIntent
            )
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    notiIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setColor(-16711921)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || intent.action == null) {
            return super.onStartCommand(intent, flags, startId)
        }

        when (intent.action) {
            "START" -> {
                app.timerListener = this
                updateNotificationStep()
                startForeground(TIMER_ID, builder?.build())
                if (app.overDue){
                    startOverDueTimer()
                }
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


    lateinit var overDueTimer : Timer
    private fun startOverDueTimer(){
        overDueTimer = Timer()

        overDueTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (!app.overDue){
                    overDueTimer.cancel()
                } else{
                    notifyUser()
                }
            }
        },OVERDUE_TIMER_DELAY, OVERDUE_TIMER_DELAY)
    }


    fun updateNotificationStep(){
        val activeStep = app.activeStep!!

        builder?.color =  if(app.overDue) -60892 else -16711921
        builder?.setContentTitle(activeStep.text)
        everySecond(activeStep.duration)
    }

    fun notifyUser(){
        try {
            val notification: Uri = Uri.parse("android.resource://com.hlag.routine/raw/alert");
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(140, VibrationEffect.EFFECT_DOUBLE_CLICK))
        }
        else{
            vibrator.vibrate(140)
        }
    }


    override fun everySecond(secsLeft: Int) {
        builder?.setContentText(secsLeft.toString())
        mNotificationManager!!.notify(TIMER_ID, builder!!.build())
    }

    override fun onFinished() {
        if(app.activeStep?.duration != 0){
            builder?.color = -60892
            mNotificationManager!!.notify(TIMER_ID, builder!!.build())
            notifyUser()
            startOverDueTimer()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        overDueTimer.cancel()
        mNotificationManager?.cancel(TIMER_ID)
    }

    override fun onAllStepsFinished() {
    }
}