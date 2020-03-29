package com.hlag.routine

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


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

}