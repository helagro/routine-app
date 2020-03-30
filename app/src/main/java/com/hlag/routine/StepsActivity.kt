package com.hlag.routine

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
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


class StepsActivity : AppCompatActivity(), MyRecyclerViewAdapter.ItemClickListener,
    MyApp.TimerListeners {
    val TAG = "StepsActivity"
    lateinit var routine: Routine


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)
        setSupportActionBar(toolbar)

        val routineName: String? = intent.getStringExtra("routine_name")
        title = routineName
        readFile(routineName)

        steps_list.layoutManager = LinearLayoutManager(this)
        val stepAdapter = MyRecyclerViewAdapter(this, routine.steps)
        stepAdapter.setClickListener(this)
        steps_list.adapter = stepAdapter

        //reorganize setup
        val itemTouchHelperCallbackList =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    Collections.swap(
                        routine.steps,
                        viewHolder.adapterPosition,
                        target.adapterPosition
                    )
                    stepAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    TODO("Not yet implemented")
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallbackList)
        itemTouchHelper.attachToRecyclerView(steps_list)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        steps_list.addItemDecoration(divider)

        fab.setOnClickListener { view ->
            routine.steps.add(Step(0, "S"))
            steps_list.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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
                routine.steps.forEach { step -> step.checked = false }
                steps_list.adapter?.notifyDataSetChanged()
                return true
            }
            R.id.action_start_timer -> {
                (application as MyApp).nextStep()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun readFile(name: String?) {
        name?.let {
            val text = StringBuilder()

            try {
                val br = BufferedReader(FileReader(File(MyApp.dir, name + ".txt")))

                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            routine = Gson().fromJson<Routine>(text.toString(), Routine::class.java)
            (application as MyApp).activeRoutine = routine
        }?: run {
            routine = (application as MyApp).activeRoutine
        }
    }


    override fun onResume() {
        (application as MyApp).timerListener = this
        if (isMyServiceRunning(RoutineService::class.java)) {
            steps_list.adapter?.notifyDataSetChanged()

            val intent = Intent(this, RoutineService::class.java)
            intent.action = "PAUSE"
            startService(intent)
        }
        super.onResume()
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    override fun onItemClick(view: View?, position: Int) {
        val stepDialog = StepEditDialog(this)
        val step = routine.steps.get(position)

        stepDialog.step_name_edit.setText(step.text)
        stepDialog.step_minutes_edit.setText(step.duration.toString())

        stepDialog.setOnDismissListener {
            val text = stepDialog.step_name_edit.text.toString()
            val duration = stepDialog.step_minutes_edit.text.toString().toInt()

            step.text = text
            step.duration = duration

            steps_list.adapter?.notifyItemChanged(position)
        }
    }

    override fun onPause() {
        super.onPause()

        //Contact with service
        val intent = Intent(this, RoutineService::class.java)
        if ((application as MyApp).timerRunning) {
            intent.action = "START"
        } else if (isMyServiceRunning(RoutineService::class.java)) {
            intent.action = "DESTROY"
        }
        startService(intent)

        MyApp.writeRoutine(this, routine)
    }

    override fun everySecond(secsLeft: Int) {
        Log.d(TAG, secsLeft.toString())
    }

    override fun onNext() {
        steps_list.adapter!!.notifyDataSetChanged()
    }

    override fun onFinished() {

    }
}
