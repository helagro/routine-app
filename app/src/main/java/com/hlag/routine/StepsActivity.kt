package com.hlag.routine

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
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

class StepsActivity : AppCompatActivity(), MyRecyclerViewAdapter.ItemClickListener, MyApp.TimerListeners {
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

        (application as MyApp).startTimer(300)
    }

    override fun onResume() {
        (application as MyApp).timerListener = this
        super.onResume()
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
            R.id.action_uncheck ->{
                routine.steps.forEach { step -> step.checked = false}
                steps_list.adapter?.notifyDataSetChanged()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun readFile(name: String?) {
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
    }

    override fun onItemClick(view: View?, position: Int) {
        val textDialog = TextDialog(this)
        textDialog.editText.setText(routine.steps.get(position).text)
        textDialog.setOnDismissListener {
            val text = textDialog.editText.text.toString()
            routine.steps.get(position).text = text
            steps_list.adapter?.notifyItemChanged(position)
        }
    }

    override fun onPause() {
        super.onPause()

        MyApp.writeRoutine(this, routine)
    }

    override fun everySecond(secsLeft: Int) {
        Log.d("tag", "left:" + secsLeft)
    }



    override fun onFinish() {
        TODO("Not yet implemented")
    }
}
