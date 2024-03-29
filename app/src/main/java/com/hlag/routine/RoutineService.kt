package com.hlag.routine

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat


class RoutineService : Service(), RoutinePlayer.TimerListeners {
    companion object {
        const val COMPLETE_ACTION = "complete-action"

        const val TIMER_ID = 1
        const val OVERDUE_TIMER_DELAY = 60 * 1000L //5
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) { //complete button
            val routinePlayer = RoutinePlayer.getInstance()
            routinePlayer.nextStep()

            routinePlayer.activeStep?.let {
                updateNotificationStep()

            } ?: run {//Done all steps
                stopSelf()
            }
        }
    }

    private lateinit var snoozeIntent: Intent
    private lateinit var builder: NotificationCompat.Builder
    private var mNotificationManager: NotificationManager? = null
    lateinit var routinePlayer: RoutinePlayer


    override fun onCreate() {
        super.onCreate()
        routinePlayer = RoutinePlayer.getInstance()

        //receiver
        val filter = IntentFilter()
        filter.addAction(COMPLETE_ACTION)
        registerReceiver(notificationReceiver, filter)

        //notification
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notiIntent = Intent(applicationContext, StepsActivity::class.java)
        snoozeIntent = Intent(this, SnoozeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val completePendingIntent =
            PendingIntent.getBroadcast(this, 0, Intent().setAction(COMPLETE_ACTION), 0)
        val snoozePendingIntent =
            PendingIntent.getActivity(applicationContext, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = NotificationCompat.Builder(applicationContext, MyApp.ROUTINE_PLAYER)
            .setSmallIcon(R.drawable.ic_add_black_24dp)
            .setContentText("Text")
            .addAction(
                R.drawable.ic_check_box_outline_blank_black_24dp,
                "Complete",
                completePendingIntent
            )
            .addAction(
                R.drawable.ic_check_box_outline_blank_black_24dp,
                "Snooze",
                snoozePendingIntent
            )
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    notiIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setColor(-16711921)
            .setOnlyAlertOnce(true)
            .setDefaults(Notification.DEFAULT_ALL)
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
                routinePlayer.timerListener = this
                updateNotificationStep()
                startForeground(TIMER_ID, builder.build())
                if (routinePlayer.overDue) {
                    //startOverDueTimer()
                }
            }
            "PAUSE" -> {
                stopForeground(true)
                mNotificationManager?.cancel(TIMER_ID)
            }

            "DESTROY" -> {
                startForeground(TIMER_ID, builder.build())
                stopForeground(true)
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    fun updateNotificationStep() {
        val activeStep = routinePlayer.activeStep!!

        builder.color = if (routinePlayer.overDue) -60892 else -16711921
        builder.setContentTitle(activeStep.text)
        everySecond(activeStep.duration)
    }

    override fun everySecond(secsLeft: Int) {
        builder.setContentText(GeneralHelpers.secToStr(secsLeft))
        mNotificationManager!!.notify(TIMER_ID, builder.build())
    }

    override fun onStarted() {

    }

    override fun onStepTimerFinished() {
        if (routinePlayer.activeStep?.duration != 0) {
            builder.color = -60892
            routinePlayer.alertUser(this)

            startActivity(snoozeIntent)
        }
    }

    override fun onRoutineFinished() {
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        mNotificationManager?.cancel(TIMER_ID)
    }
}