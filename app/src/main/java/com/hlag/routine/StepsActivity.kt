package com.hlag.routine

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_steps.*
import kotlinx.android.synthetic.main.step_edit_dialog.*
import java.util.*
import kotlin.math.floor


class StepsActivity : AppCompatActivity(), StepsAdapter.ItemClickListener,
    StepsAdapter.StepAdapterListener,
    RoutinePlayer.TimerListeners, View.OnClickListener {
    private val TAG = "StepsActivity"
    lateinit var routinePlayer: RoutinePlayer
    lateinit var routine: Routine


    override fun onCreate(savedInstanceState: Bundle?) {
        //Standard activity things
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        routinePlayer = RoutinePlayer.getInstance()

        val routineName: String? = intent.getStringExtra("routine_name")
        loadRoutine(routineName)

        //setup view
        title = routineName
        ViewFacory.setupRecycler(this, steps_list)

        step_check_view.setOnClickListener(this)
        step_name_view.setTextIsSelectable(true)
        fab.setOnClickListener(this)
        changeUiBasedOnRoutinePlayerIsRunning()
    }

    private fun loadRoutine(routineName: String?) {
        routineName?.let {
            routine = FileManager.readFile(routineName)!!
            routinePlayer.routine = routine
        } ?: run {
            routine = routinePlayer.routine
        }
    }

    private fun changeUiBasedOnRoutinePlayerIsRunning(){
        if(routinePlayer.isRunning)
            step_check_view.setImageDrawable(getDrawable(R.drawable.ic_check_box_outline_blank_black_24dp))
        else
            step_check_view.setImageDrawable(getDrawable(R.drawable.play_arrow_black))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_steps, menu)
        return true
    }

    override fun onResume() {
        routinePlayer.timerListener = this
        if (GeneralHelpers.isMyServiceRunning(this, RoutineService::class.java)) {
            steps_list.adapter?.notifyDataSetChanged()
            updatePlayer()

            GeneralHelpers.startForeground(this, RoutineService::class.java, "PAUSE")
        }
        super.onResume()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_uncheck -> {
                routinePlayer.uncheckAllSteps()
                steps_list.adapter?.notifyDataSetChanged()
                return true
            }
            R.id.action_start_timer -> {
                routinePlayer.finishRoutine()
                changeUiBasedOnRoutinePlayerIsRunning()
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
        if(routinePlayer.isRunning){
            routinePlayer.beginStep(step, step.duration)
            updatePlayer()
        } else {
            routinePlayer.startRoutine()
        }

    }

    override fun onItemChecked(step: Step?) {
        if (step!! == routinePlayer.activeStep) {
            nextStep()
        }
    }


    /*
    From Timer
     */
    override fun everySecond(secsLeft: Int) {
        step_time_view.text = GeneralHelpers.secToStr(secsLeft)
    }

    override fun onStarted() {
        changeUiBasedOnRoutinePlayerIsRunning()
    }

    override fun onStepTimerFinished() {
    }

    override fun onRoutineFinished() {
        changeUiBasedOnRoutinePlayerIsRunning()
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
            step_check_view -> {
                if(routinePlayer.isRunning){
                    nextStep()
                } else {
                    routinePlayer.startRoutine()
                    steps_list.adapter!!.notifyDataSetChanged()
                    updatePlayer()
                }
            }
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
        routinePlayer.nextStep()
        updatePlayer()
    }

    private fun updatePlayer() {
        step_name_view.text = routinePlayer.activeStep?.text
        step_time_view.text = routinePlayer.activeStep?.duration.toString()
    }


    /*
    Closing cycle
     */
    override fun onPause() {
        super.onPause()

        //Contact with service
        if (routinePlayer.activeStep != null) {
            GeneralHelpers.startForeground(this, RoutineService::class.java, "START")

        } else if (GeneralHelpers.isMyServiceRunning(this, RoutineService::class.java)) {
            GeneralHelpers.startForeground(this, RoutineService::class.java, "DESTROY")
        }


        FileManager.writeRoutine(this, routine)
    }

}
