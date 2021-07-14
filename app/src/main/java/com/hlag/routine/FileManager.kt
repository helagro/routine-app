package com.hlag.routine

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.io.*

class FileManager {
    companion object {
        var dir = "/storage/emulated/0"

        fun readFile(name: String?): Routine? {
            val text = StringBuilder()

            try {
                val br = BufferedReader(FileReader(File(dir, name + ".txt")))

                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return Gson().fromJson<Routine>(text.toString(), Routine::class.java)

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
}