package com.hlag.routine

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*


class RoutineService : Service(), MyApp.TimerListeners, TextToSpeech.OnInitListener {
    companion object {
        const val TAG = "RoutineService"
        const val COMPLETE_ACTION = "complete-action"

        const val TIMER_ID = 1
        const val OVERDUE_TIMER_DELAY = 60 * 1000L //5
    }

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

    private lateinit var vibrator: Vibrator
    private lateinit var am: AudioManager
    private lateinit var tts: TextToSpeech
    private var builder: NotificationCompat.Builder? = null
    private var mNotificationManager: NotificationManager? = null
    lateinit var app: MyApp


    override fun onCreate() {
        super.onCreate()
        app = application as MyApp
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        //tts
        am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        tts = TextToSpeech(this, this)
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            //private var curVol = 0
            override fun onStart(utteranceId: String?) {
                am.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)

                //curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                //am.setStreamVolume(AudioManager.STREAM_MUSIC, 12, 0)
            }

            override fun onError(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                //am.setStreamVolume(AudioManager.STREAM_MUSIC, curVol, 0)
                am.abandonAudioFocus(null)
            }
        })

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
                if (app.overDue) {
                    startOverDueTimer()
                }
            }
            "PAUSE" -> {
                stopForeground(true)
                mNotificationManager?.cancel(TIMER_ID)
            }

            "DESTROY" -> {
                startForeground(TIMER_ID, builder?.build())
                stopForeground(true)
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    var overDueTimer: Timer? = null
    var missed = 1
    private fun startOverDueTimer() {
        overDueTimer?.cancel()
        overDueTimer = Timer()

        overDueTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (!app.overDue) {
                    overDueTimer!!.cancel()
                } else {
                    missed += 1
                    notifyUser()
                }
            }
        }, OVERDUE_TIMER_DELAY, OVERDUE_TIMER_DELAY)
    }


    fun updateNotificationStep() {
        val activeStep = app.activeStep!!

        builder?.color = if (app.overDue) -60892 else -16711921
        builder?.setContentTitle(activeStep.text)
        everySecond(activeStep.duration)
        missed = 1
    }

    fun notifyUser() {
        val map: HashMap<String, String> = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "UniqueID"

        tts.speak(
            missed.toString(),
            TextToSpeech.QUEUE_FLUSH, map
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    200,
                    VibrationEffect.EFFECT_DOUBLE_CLICK
                )
            )
        } else {
            vibrator.vibrate(200)
        }
    }


    //TTS
    override fun onInit(status: Int) { //TTS
        if (status == TextToSpeech.SUCCESS) {
            val result: Int = tts.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
            }
        }
    }

    override fun everySecond(secsLeft: Int) {
        builder?.setContentText(secsLeft.toString())
        mNotificationManager!!.notify(TIMER_ID, builder!!.build())
    }

    override fun onFinished() {
        if (app.activeStep?.duration != 0) {
            builder?.color = -60892
            mNotificationManager!!.notify(TIMER_ID, builder!!.build())
            notifyUser()
            startOverDueTimer()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        overDueTimer?.cancel()
        mNotificationManager?.cancel(TIMER_ID)
        tts.stop()
        tts.shutdown()
    }

    override fun onAllStepsFinished() {
    }

}