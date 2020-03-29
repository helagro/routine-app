package com.hlag.routine

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.routine_grid_item.*


class RoutineActivity : AppCompatActivity(){
    companion object {
        fun launch(context: Context, routine: Routine){
            context.startActivity(Intent(context, RoutineActivity::class.java).putExtra("routine", routine))
        }
    }

    lateinit var routine: Routine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.routine_activity)

        routine = intent.getParcelableExtra("routine")
        title = routine.name

    }
}