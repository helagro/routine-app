package com.hlag.routine

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_steps.*
import java.util.*

class StepsActivity : AppCompatActivity(), MyRecyclerViewAdapter.ItemClickListener{
    lateinit var routine: Routine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)
        setSupportActionBar(toolbar)

        routine = intent.getParcelableExtra("routine")
        title = routine.name

        steps_list.layoutManager = LinearLayoutManager(this)
        val stepAdapter = MyRecyclerViewAdapter(this, routine.steps)
        stepAdapter.setClickListener(this)
        steps_list.adapter = stepAdapter

        val itemTouchHelperCallbackList = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                Collections.swap(routine.steps, viewHolder.adapterPosition, target.adapterPosition)
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
            addStep()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {return true}
            R.id.action_delete -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun addStep(){
        routine.steps.add(Step(0, "Step"))
        steps_list.adapter?.notifyDataSetChanged()
    }

    override fun onItemClick(view: View?, position: Int) {
        var textDialog = TextDialog(this)
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
}
