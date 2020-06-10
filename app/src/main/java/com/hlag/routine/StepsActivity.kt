package com.hlag.routine

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_steps.*
import kotlinx.android.synthetic.main.step_edit_dialog.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.math.floor


class StepsActivity : AppCompatActivity(), StepsAdapter.ItemClickListener,
    StepsAdapter.StepAdapterListener,
    MyApp.TimerListeners, View.OnClickListener {
    val TAG = "StepsActivity"
    lateinit var app: MyApp
    lateinit var routine: Routine
    var startTime = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        //Standard activity things
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        app = (application as MyApp)

        //get routine
        val routineName: String? = intent.getStringExtra("routine_name")
        routineName?.let {
            routine = FileManager.readFile(routineName)!!
            app.activeRoutine = routine
        } ?: run {
            routine = app.activeRoutine
        }

        //setup view
        title = routineName
        ViewFacory.setupRecycler(this, steps_list)

        step_check_view.setOnClickListener(this)
        step_name_view.setTextIsSelectable(true)
        fab.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_steps, menu)
        return true
    }

    override fun onResume() {
        app.timerListener = this
        if (GeneralHelpers.isMyServiceRunning(this, RoutineService::class.java)) {
            steps_list.adapter?.notifyDataSetChanged()
            updatePlayer()

            GeneralHelpers.startForeground(this, RoutineService::class.java, "PAUSE")
        }
        super.onResume()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                return true
            }
            R.id.action_delete -> {
                return true
            }
            R.id.action_uncheck -> {
                routine.steps.forEachIndexed { i, step ->
                    step.checked = false
                }

                steps_list.post {
                    steps_list.adapter!!.notifyDataSetChanged()
                }

                return true
            }
            R.id.action_start_timer -> {
                app.stopTimer()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*
    From list
    */
    override fun onItemClick(view: View?, position: Int) {
        val stepDialog = StepEditDialog(this)
        val step = routine.steps.get(position)

        stepDialog.step_name_edit.setText(step.text)
        stepDialog.step_minutes_edit.setText(floor(step.duration / 60f).toInt().toString())
        stepDialog.step_seconds_edit.setText((step.duration % 60).toString())

        stepDialog.setOnDismissListener {
            if (stepDialog.delete) {
                routine.steps.remove(step)
                steps_list.adapter?.notifyItemRemoved(position)
            } else {
                step.text = stepDialog.step_name_edit.text.toString()
                step.duration = stepDialog.getDuration()

                steps_list.adapter?.notifyItemChanged(position)
            }
        }
    }

    override fun onItemPlayPressed(step: Step) {
        startTime = Calendar.getInstance().get(Calendar.MINUTE)
        Log.d(TAG, "test:" + startTime)
        app.activeStep = step
        app.startTimer(step)
        updatePlayer()
    }

    override fun onItemChecked(step: Step?) {
        if (step!! == app.activeStep) {
            nextStep()
        }
    }


    /*
    From Timer
     */
    override fun everySecond(secsLeft: Int) {
        step_time_view.text = secsLeft.toString()
    }

    override fun onFinished() {

    }

    override fun onAllStepsFinished() {

    }



    /*
    Other ui
     */

    //Back arrow
    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    override fun onClick(v: View?) {
        when (v) {
            step_check_view -> nextStep()
            fab -> {
                routine.steps.add(Step(0, ""))
                steps_list.adapter?.notifyDataSetChanged()
            }
        }
    }


    /*
    Helpers
     */
    private fun nextStep() {
        steps_list.adapter!!.notifyDataSetChanged()
        app.nextStep()
        updatePlayer()
    }

    private fun updatePlayer(){
        step_name_view.text = app.activeStep!!.text
        step_time_view.text = app.activeStep?.duration.toString()
    }


    /*
    Closing cycle
     */
    override fun onPause() {
        super.onPause()

        //Contact with service
        if (app.timed) {
            GeneralHelpers.startForeground(this, RoutineService::class.java, "START")

        } else if (GeneralHelpers.isMyServiceRunning(this, RoutineService::class.java)) {
            GeneralHelpers.startForeground(this, RoutineService::class.java, "DESTROY")
        }


        FileManager.writeRoutine(this, routine)
    }


}
